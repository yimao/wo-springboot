package com.mudcode.springboot.es.test;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.es.ESQueryService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;

public class ESIndexAdminServiceTest extends ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ESIndexAdminServiceTest.class);

    @Autowired
    private ESQueryService queryService;

    @Test
    public void test() throws IOException {
        RestHighLevelClient client = queryService.getClient();
        GetIndexRequest indexRequest = new GetIndexRequest("*");
        GetIndexResponse response = client.indices().get(indexRequest, RequestOptions.DEFAULT);
        logger.info("getIndices(): {}", Arrays.toString(response.getIndices()));
    }

}
