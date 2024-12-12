package com.mudcode.springboot.es.test;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.es.ESQueryService;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ESQueryServiceTest extends ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ESQueryServiceTest.class);

    @Autowired
    private ESQueryService queryService;

    @Test
    public void test() throws IOException {
        RestHighLevelClient client = queryService.getClient();
        GetRequest request = new GetRequest();
        request.index("svr_action_trace_20210822");
        request.id("51674947981");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        logger.info("getSourceAsString: {}", response.getSourceAsString());
    }

    @Test
    public void test1() throws IOException {
        RestHighLevelClient client = queryService.getClient();
        SearchRequest request = new SearchRequest();
        request.indices("svr_action_trace_20210822");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();

        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery();
        idsQueryBuilder.addIds("51674947981", "51674947957", "51674947945");
        booleanQuery.must(idsQueryBuilder);

        builder.query(booleanQuery);
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits()) {
            logger.info("getSourceAsString: {}", hit.getSourceAsString());
        }
    }

}
