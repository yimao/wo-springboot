package com.mudcode.springboot.kafka.consumer;

import com.mudcode.springboot.common.util.ExecutorServiceUtil;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerHolder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerHolder.class);

    private final KafkaConsumer<String, String> kafkaConsumer;

    @Setter
    private boolean commitAsync = false;

    @Setter
    private int pollTimeout = 500;

    @Setter
    private int threads = 1;

    private AtomicBoolean running;

    private ExecutorService executorService;

    public KafkaConsumerHolder(KafkaConsumer<String, String> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    public void init() {
        this.running = new AtomicBoolean(true);
        this.executorService = ExecutorServiceUtil.newThreadPoolExecutor(threads, threads, "kafka-consumer");
        this.executorService.execute(this::run);
    }

    public void destroy() {
        this.running.set(false);
        ExecutorServiceUtil.stop(executorService, 10);
    }

    private void run() {
        while (this.running.get()) {
            try {
                ConsumerRecords<String, String> records = this.kafkaConsumer.poll(Duration.ofMillis(pollTimeout));
                if (!records.isEmpty()) {
                    try {
                        records.forEach(record -> {
                            // todo produce record
                            logger.info("Received message: {}", record.toString());
                        });
                        commit();
                    } catch (Exception ex) {
                        logger.error("error to handle records: {}", ex.getMessage(), ex);
                    }
                }
            } catch (Exception ex) {
                logger.error("error to fetch records from kafka: {}", ex.getMessage(), ex);
            }
        }
    }

    private void commit() {
        if (commitAsync) {
            this.kafkaConsumer.commitAsync((Map<TopicPartition, OffsetAndMetadata> committedOffsets, Exception e) -> {
                if (e != null) {
                    logger.error("error to commit offsets to kafka: {}", e.getMessage(), e);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("committed offsets to kafka(async): {}", committedOffsets);
                }
            });
        } else {
            try {
                this.kafkaConsumer.commitSync();
            } catch (Exception e) {
                logger.error("error to commit offsets to kafka(sync): {}", e.getMessage(), e);
            }
        }
    }

}
