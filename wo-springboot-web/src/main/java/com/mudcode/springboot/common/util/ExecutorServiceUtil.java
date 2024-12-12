package com.mudcode.springboot.common.util;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceUtil {

    private ExecutorServiceUtil() {
    }

    public static void stop(ExecutorService executor, int awaitSeconds) {
        if (executor != null) {
            try {
                executor.shutdown();
                if (awaitSeconds > 0) {
                    if (!executor.awaitTermination(awaitSeconds, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } else {
                    executor.shutdownNow();
                }
            } catch (Exception ignored) {
                // ignored
            }
        }
    }

    public static void stop(ExecutorService executor) {
        stop(executor, 0);
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String threadName) {
        return Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(int threads, int workQueueSize, String threadGroupName) {
        return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(workQueueSize), new CustomThreadFactory(threadGroupName),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * copy from java.util.concurrent.Executors.DefaultThreadFactory
     */
    public static class CustomThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        public CustomThreadFactory(String name) {
            Objects.requireNonNull(name);
            group = Thread.currentThread().getThreadGroup();
            namePrefix = name + "-p" + poolNumber.getAndIncrement() + "-t";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }

    }

}
