package com.mudcode.springboot.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;

public class ClickHouseWriterMonitor {

    private final MeterRegistry meterRegistry = Metrics.globalRegistry;

    public void onBulkWriteCompleted(boolean success, int agreementId, String table, int durationMs,
                                     int recordCountInBatch, long totalSizeInBatch) {
        List<Tag> tags = buildTags(success, agreementId, table);

        String bulkWriteDurationMetric = "clickhouse_writer_bulk_write_duration";
        this.meterRegistry.summary(bulkWriteDurationMetric, tags).record(durationMs);

        String bulkWriteBytes = "clickhouse_writer_bulk_write_bytes";
        this.meterRegistry.summary(bulkWriteBytes, tags).record(totalSizeInBatch);
    }

    private List<Tag> buildTags(boolean success, int agreementId, String table) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("agreementId", String.valueOf(agreementId)));
        tags.add(Tag.of("table", table));
        tags.add(Tag.of("status", success ? "success" : "failure"));
        return tags;
    }

}
