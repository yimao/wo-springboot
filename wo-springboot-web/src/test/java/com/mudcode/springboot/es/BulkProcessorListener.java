package com.mudcode.springboot.es;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BulkProcessorListener implements BulkProcessor.Listener {

    private static final Logger logger = LoggerFactory.getLogger(BulkProcessorListener.class);

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
        long bulkRequestRamBytesUsed = request.ramBytesUsed();
        long bulkRequestEstimatedSizeInBytes = request.estimatedSizeInBytes();
        String description = request.getDescription();
        logger.debug("Bulk#{}, {}, BulkRequestEstimatedSizeInBytes: {}, BulkRequestRamBytesUsed: {}", executionId,
                description, bulkRequestEstimatedSizeInBytes, bulkRequestRamBytesUsed);
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        if (response.hasFailures()) {
            logger.error("Bulk#{} executed with failures, error: {}", executionId, response.buildFailureMessage());
        } else {
            logger.debug("Bulk#{} completed in {} milliseconds", executionId, response.getTook().getMillis());
        }
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        logger.error("Bulk#{} executed with failures, error: {}, {}", executionId, failure.getClass().getName(),
                failure.getMessage(), failure);
    }

}
