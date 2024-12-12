package com.mudcode.springboot.es;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ESIndexRequestService extends AbstractBaseClient {

    @Autowired
    private ESProperties configuration;

    private BulkProcessor bulkProcessor;

    private RestHighLevelClient client;

    @PostConstruct
    public void init() throws Exception {
        this.client = buildClient(configuration);
        this.bulkProcessor = BulkProcessor
                .builder((request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                        new BulkProcessorListener(), "ESBulkProcessor")
                .setBulkActions(configuration.getBulkActions())
                .setBulkSize(new ByteSizeValue(configuration.getBulkSizeInBytes(), ByteSizeUnit.BYTES))
                .setFlushInterval(TimeValue.timeValueSeconds(configuration.getFlushInterval()))
                .setConcurrentRequests(configuration.getConcurrentRequests())
                .build();
    }

    @PreDestroy
    public void destroy() {
        bulkProcessor.flush();
        try {
            bulkProcessor.awaitClose(10, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            // ignored
        }
        closeClient(this.client);
    }

    public void indexRequest(String indexName, String docId, String json) {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(indexName);
        if (docId != null) {
            indexRequest.id(docId);
        }
        indexRequest.source(json, XContentType.JSON);
        bulkProcessor.add(indexRequest);
    }

    public void deleteRequest(String indexName, String docId) {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index(indexName);
        deleteRequest.id(docId);
        bulkProcessor.add(deleteRequest);
    }

    public void updateRequest(String indexName, String docId, String json) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);
        updateRequest.id(docId);
        updateRequest.retryOnConflict(1);
        updateRequest.doc(json, XContentType.JSON);
        updateRequest.docAsUpsert(false);
        bulkProcessor.add(updateRequest);
    }

}
