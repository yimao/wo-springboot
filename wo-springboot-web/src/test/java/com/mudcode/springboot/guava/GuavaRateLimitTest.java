package com.mudcode.springboot.guava;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class GuavaRateLimitTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    public void init() {
        this.rateLimiter = RateLimiter.create(0.1);
    }

    @Test
    public void testQps() throws InterruptedException {
        for (int i = 0; i < 500; i++) {
            String time = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            boolean acquire = this.rateLimiter.tryAcquire();
            System.out.printf("%s %d %s\n", time, i, acquire);
            Thread.sleep(300);
        }
    }

}
