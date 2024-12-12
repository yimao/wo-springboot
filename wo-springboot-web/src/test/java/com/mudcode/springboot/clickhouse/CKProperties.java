package com.mudcode.springboot.clickhouse;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Data
@ConfigurationProperties(prefix = "mudcode.clickhouse")
@Configuration
public class CKProperties {

    private final int DEFAULT_LOCAL_QUEUE_SIZE = 10_000;

    private final int DEFAULT_LOCAL_QUEUE_FLUSH_PERIOD_SECONDS = 10;

    private final int DEFAULT_TIMEOUT_MS = 10_000;

    private List<String> endpoints;

    private String cluster;

    private String database;

    private String username;

    private String password;

    private int localQueueSize = DEFAULT_LOCAL_QUEUE_SIZE;

    private int schedulePeriod = DEFAULT_LOCAL_QUEUE_FLUSH_PERIOD_SECONDS;

    private int connectionTimeout = DEFAULT_TIMEOUT_MS;

    private int socketTimeout = DEFAULT_TIMEOUT_MS;

    private int connectionRequestTimeout = DEFAULT_TIMEOUT_MS;

    private Properties pops;

}
