package com.mudcode.springboot.kafka.producer;

import com.mudcode.springboot.ApplicationTest;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

class KafkaProducerTest extends ApplicationTest {

    @Value("${kafka.topic.app-message}")
    private String topic;

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    private KafkaTemplate<String, String> stringKafkaTemplate;

    @Test
    void send() throws InterruptedException {
        int index = 100_000;
        while (index > 0) {
            String str = UUID.randomUUID().toString();
            this.kafkaProducer.send(new ProducerRecord<>(this.topic, str));
            this.stringKafkaTemplate.send(this.topic, str);
            index--;
        }
        Thread.sleep(10_000);
    }

}
