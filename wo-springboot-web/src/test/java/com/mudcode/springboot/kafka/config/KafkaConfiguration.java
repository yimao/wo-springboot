package com.mudcode.springboot.kafka.config;

import com.mudcode.springboot.kafka.consumer.KafkaConsumerHolder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @ConfigurationProperties("kafka.consumer")
    @Bean("kafkaConsumerProperties")
    public Properties kafkaConsumerProperties() {
        return new Properties();
    }

    @ConfigurationProperties("kafka.producer")
    @Bean("kafkaProducerProperties")
    public Properties kafkaProducerProperties() {
        return new Properties();
    }

    @Bean(name = "kafkaProducer", destroyMethod = "close")
    public KafkaProducer<String, String> kafkaProducer(@Qualifier("kafkaProducerProperties") Properties properties) {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaProducer<>(properties);
    }

    @Bean(name = "appMessageKafkaConsumer", destroyMethod = "close")
    public KafkaConsumer<String, String> kafkaConsumer(@Qualifier("kafkaConsumerProperties") Properties properties,
                                                       @Value("${kafka.topic.app-message}") String topic) {
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(List.of(topic));
        return kafkaConsumer;
    }

    @Bean(name = "appKafkaConsumerHolder", initMethod = "init", destroyMethod = "destroy")
    public KafkaConsumerHolder appKafkaConsumerHolder(
            @Qualifier("appMessageKafkaConsumer") KafkaConsumer<String, String> kafkaConsumer) {
        return new KafkaConsumerHolder(kafkaConsumer);
    }

}
