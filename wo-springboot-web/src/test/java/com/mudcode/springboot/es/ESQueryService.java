package com.mudcode.springboot.es;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ESQueryService extends AbstractBaseClient {

    @Autowired
    private ESProperties configuration;

    private RestHighLevelClient client;

    public RestHighLevelClient getClient() {
        return client;
    }

    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    @PostConstruct
    public void init() throws Exception {
        this.client = buildClient(configuration);
    }

    @PreDestroy
    public void destroy() {
        closeClient(this.client);
    }

}
