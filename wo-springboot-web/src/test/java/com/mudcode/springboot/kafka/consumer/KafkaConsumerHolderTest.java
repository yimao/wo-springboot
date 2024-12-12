package com.mudcode.springboot.kafka.consumer;

import com.mudcode.springboot.ApplicationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class KafkaConsumerHolderTest extends ApplicationTest {

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(30).toMillis());
    }

}
