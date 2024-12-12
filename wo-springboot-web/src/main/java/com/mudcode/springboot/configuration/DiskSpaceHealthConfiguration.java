package com.mudcode.springboot.configuration;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthIndicatorProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.File;

@Configuration
@ConditionalOnEnabledHealthIndicator("diskspace")
@AutoConfigureBefore({DiskSpaceHealthContributorAutoConfiguration.class})
@EnableConfigurationProperties(DiskSpaceHealthIndicatorProperties.class)
public class DiskSpaceHealthConfiguration {
    @Bean(name = "diskSpaceHealthIndicator", initMethod = "init")
    public DiskSpaceHealthIndicator diskSpaceHealthIndicator(DiskSpaceHealthIndicatorProperties properties) {
        return new DiskSpaceHealthIndicator(properties.getPath(), properties.getThreshold());
    }

    public static class DiskSpaceHealthIndicator extends org.springframework.boot.actuate.system.DiskSpaceHealthIndicator {
        public DiskSpaceHealthIndicator(File path, DataSize threshold) {
            super(path, threshold);
        }

        public void init() {
            HealthMetric.reg("diskspace", this, DiskSpaceHealthIndicator::healthCode);
        }

        private double healthCode() {
            return HealthMetric.status(this.health().getStatus());
        }
    }
}
