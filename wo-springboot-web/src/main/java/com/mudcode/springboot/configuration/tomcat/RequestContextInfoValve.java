package com.mudcode.springboot.configuration.tomcat;

import com.sun.management.ThreadMXBean;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import jakarta.servlet.ServletException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class RequestContextInfoValve extends ValveBase {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextInfoValve.class);

    private final CompositeMeterRegistry meterRegistry = Metrics.globalRegistry;

    public RequestContextInfoValve() {
    }

    public RequestContextInfoValve(boolean asyncSupported) {
        super(asyncSupported);
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        Thread thread = Thread.currentThread();
        long allocatedBytes = threadMXBean.getThreadAllocatedBytes(thread.getId());
        long start = System.currentTimeMillis();

        // Ask the next valve to process the request.
        getNext().invoke(request, response);

        long end = System.currentTimeMillis();
        long allocatedBytesByProcessed = threadMXBean.getThreadAllocatedBytes(thread.getId());

        int duration = (int) (end - start);
        int memory = (int) (allocatedBytesByProcessed - allocatedBytes);

        try {
            Set<Tag> tags = new HashSet<>();
            tags.add(Tag.of("contextPath", contextPath));
            tags.add(Tag.of("requestURI", requestUri));
            meterRegistry.summary("tomcat.request.duration", tags).record(duration);
            meterRegistry.summary("tomcat.request.memory", tags).record(memory);

            Timer.builder("tomcat.request.duration.quantile")
                    .tags(tags)
                    .publishPercentiles(0.5, 0.90, 0.95, 0.99)
                    .publishPercentileHistogram(false)
                    .minimumExpectedValue(Duration.ofMillis(1))
                    .maximumExpectedValue(Duration.ofSeconds(120))
                    .register(meterRegistry)
                    .record(Duration.ofMillis(duration));

        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

}
