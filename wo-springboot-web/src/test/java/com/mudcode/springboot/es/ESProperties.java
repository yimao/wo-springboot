package com.mudcode.springboot.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("mudcode.es")
public class ESProperties {

    private static final int DEFAULT_BULK_ACTIONS = 1000;

    private static final int DEFAULT_BULK_SIZE = 10240; // 10MB

    private static final int DEFAULT_FLUSH_INTERVAL = 1;

    private static final int DEFAULT_CONCURRENT_REQUEST = 8;

    private List<String> hosts;

    private String username;

    private String password;

    private int bulkActions = DEFAULT_BULK_ACTIONS;

    private int bulkSizeInBytes = DEFAULT_BULK_SIZE;

    private int flushInterval = DEFAULT_FLUSH_INTERVAL;

    private int concurrentRequests = DEFAULT_CONCURRENT_REQUEST;

}
