package com.mudcode.springboot.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Order
@Configuration
public class HealthMetric {
    public static final String name = "management.health.indicator";

    public static final MeterRegistry meterRegistry = Metrics.globalRegistry;

    private final List<HealthIndicator> healthIndicatorList;

    public HealthMetric(@Autowired List<HealthIndicator> healthIndicatorList) {
        this.healthIndicatorList = healthIndicatorList;
    }

    public static <T> void reg(String scope, T t, ToDoubleFunction<T> valueFunction) {
        List<Tag> tags = Collections.singletonList(Tag.of("scope", scope));
        HealthMetric.meterRegistry.gauge(HealthMetric.name, tags, t, valueFunction);
    }

    public static double status(Status status) {
        return Status.UP.equals(status) ? 1 : 0;
    }

    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(healthIndicatorList)) {
            return;
        }
        HealthMetric.reg("global", this, HealthMetric::status);
    }

    private double status() {
        StatusAggregator aggregator = StatusAggregator.getDefault();
        Status status = aggregator.getAggregateStatus(healthIndicatorList.stream()
                .map(HealthIndicator::health)
                .map(Health::getStatus)
                .collect(Collectors.toSet())
        );
        return status(status);
    }
}
