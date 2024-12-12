package com.mudcode.springboot.scheduled;

import com.mudcode.springboot.ApplicationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class ScheduledConfigTest extends ApplicationTest {

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(Duration.ofMinutes(20).toMillis());
    }

}
