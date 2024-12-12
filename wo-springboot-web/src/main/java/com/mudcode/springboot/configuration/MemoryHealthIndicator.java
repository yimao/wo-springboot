package com.mudcode.springboot.configuration;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Configuration(value = "memoryHealthIndicator")
@ConditionalOnEnabledHealthIndicator("memory")
public class MemoryHealthIndicator extends AbstractHealthIndicator {
    private static final Logger log = LoggerFactory.getLogger(MemoryHealthIndicator.class);

    private final double memoryUsedThreshold;
    private final double gcPausedThreshold;

    private GcMetricsNotificationListener gcNotificationListener;
    private List<Runnable> notificationListenerCleanUpRunnables;
    private GcMetrics gcMetrics;

    private ScheduledExecutorService scheduledExecutorService;

    public MemoryHealthIndicator(
            @Value("${health.indicator.jvm.memory-used-threshold:0.9}") double memoryUsedThreshold,
            @Value("${health.indicator.jvm.gc-paused-threshold:2000}") double gcPausedThreshold
    ) {
        this.memoryUsedThreshold = memoryUsedThreshold;
        this.gcPausedThreshold = gcPausedThreshold;
    }

    @PostConstruct
    public void init() {
        this.gcMetrics = new GcMetrics();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleWithFixedDelay(this.gcMetrics::scheduleChange, 1, 1, TimeUnit.MINUTES);

        this.gcNotificationListener = new GcMetricsNotificationListener(this.gcMetrics);
        this.notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<>();
        this.bindTo();

        HealthMetric.reg("jvm", this, MemoryHealthIndicator::healthCode);
        log.info("NBSHealthIndicator initialized");
    }

    private double healthCode() {
        return HealthMetric.status(this.health().getStatus());
    }

    @PreDestroy
    public void destroy() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
        this.scheduledExecutorService.shutdown();
        try {
            if (!this.scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                this.scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up()
                .withDetail("memory-used-threshold", memoryUsedThreshold)
                .withDetail("gc-paused-threshold", gcPausedThreshold);

        // 判断内存使用率占比
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double memUsedBytes = memoryUsage.getUsed();
        double memMaxBytes = memoryUsage.getMax();

        double memoryUsedRatio = memUsedBytes / memMaxBytes;
        builder.withDetail("memory-used-ratio", memoryUsedRatio);

        if (memoryUsedRatio > memoryUsedThreshold) {
            builder.down();
        }

        // 判断 GC 时长
        long gcTime = gcMetrics.value();
        builder.withDetail("gc-paused-duration", gcTime);
        if (gcTime > gcPausedThreshold) {
            builder.down();
        }

        Health debug = builder.build();
        if (debug.getStatus() != Status.UP) {
            log.warn("NBSHealthIndicator health check DOWN: {}", debug);
        }
    }

    private void bindTo() {
        ManagementFactory.getGarbageCollectorMXBeans()
                .forEach(gcBean -> {
                    if (gcBean instanceof NotificationEmitter notificationEmitter) {
                        notificationEmitter.addNotificationListener(this.gcNotificationListener,
                                (notification) -> notification.getType().equals("com.sun.management.gc.notification"), (Object) null
                        );
                        this.notificationListenerCleanUpRunnables.add(() -> {
                            try {
                                notificationEmitter.removeNotificationListener(this.gcNotificationListener);
                            } catch (ListenerNotFoundException ignored) {
                            }
                        });
                    }
                });
    }

    static class GcMetricsNotificationListener implements NotificationListener {
        private final GcMetrics gcMetrics;

        public GcMetricsNotificationListener(GcMetrics gcMetrics) {
            this.gcMetrics = gcMetrics;
        }

        public void handleNotification(Notification notification, Object ref) {
            CompositeData cd = (CompositeData) notification.getUserData();
            GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);
            String gcName = notificationInfo.getGcName();
            String gcCause = notificationInfo.getGcCause();
            String gcAction = notificationInfo.getGcAction();
            GcInfo gcInfo = notificationInfo.getGcInfo();
            long duration = gcInfo.getDuration(); // ms

            log.debug("GC cause: {}, name: {}, action: {}, duration: {}ms", gcCause, gcName, gcAction, duration);

            // 暂停阶段 GC
            if (!isConcurrentPhase(gcCause, gcName)) {
                this.gcMetrics.update(duration);
            }
        }

        boolean isConcurrentPhase(String cause, String name) {
            return "No GC".equals(cause) || "Shenandoah Cycles".equals(name) || name.startsWith("ZGC") && name.endsWith("Cycles") || name.startsWith("GPGC") && !name.endsWith("Pauses");
        }

    }

    static class GcMetrics {
        private final AtomicLong lastGcTime;
        private final AtomicLong currentGcTime;
        private long incrementValue;

        public GcMetrics() {
            this.lastGcTime = new AtomicLong(0L);
            this.currentGcTime = new AtomicLong(0L);
        }

        public void update(long currentGcTime) {
            this.currentGcTime.addAndGet(currentGcTime);
        }

        /**
         * 返回最近一次统计周期内的 gc 时间增量
         */
        public long value() {
            return this.incrementValue;
        }

        /**
         * 通过定时任务一分钟触发一次
         */
        public void scheduleChange() {
            long lastGcTime = this.lastGcTime.getAndSet(this.currentGcTime.get());
            long currentGcTime = this.currentGcTime.get();
            this.incrementValue = currentGcTime - lastGcTime;
        }
    }

}
