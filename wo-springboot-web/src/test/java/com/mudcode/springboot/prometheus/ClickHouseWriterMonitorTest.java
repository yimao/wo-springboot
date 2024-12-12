package com.mudcode.springboot.prometheus;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClickHouseWriterMonitorTest {

    private ClickHouseWriterMonitor clickHouseWriterMonitor;

    private PrometheusMeterRegistry prometheusRegistry;

    @BeforeEach
    public void before() {
        this.clickHouseWriterMonitor = new ClickHouseWriterMonitor();
        this.prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Metrics.addRegistry(prometheusRegistry);
    }

    @AfterEach
    public void after() {
        // Print metrics in Prometheus format
        String prometheusData = prometheusRegistry.scrape();
        System.out.println(prometheusData);
    }

    @Test
    public void testMetricMonitor() {
        clickHouseWriterMonitor.onBulkWriteCompleted(true, 1759, "default.svr_action_data", 172, 50000, 37690);

    }

}
