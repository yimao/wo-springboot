package com.mudcode.springboot.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SpringKafkaConsumerHolder {

    private static final Logger logger = LoggerFactory.getLogger(SpringKafkaConsumerHolder.class);

    @KafkaListener(topics = "${mudcode.kafka.topic.str}", groupId = "${mudcode.kafka.consumer-group.str}",
            concurrency = "${mudcode.kafka.consumer-concurrency.str}")
    public void consume(ConsumerRecord<String, String> record) {
        logger.info("Received message: {}", record.toString());
    }

}
