package com.mudcode.springboot.sampling;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class MurmurHashSamplingCounterTest {

    @Test
    void hit() {
        String traceId = "9e6fdc8dcf0ae09d55170073122600074";
        double rate = 0.2;
        System.out.println(MurmurHashSamplingCounter.INSTANCE.hit(traceId, rate));
    }

    @Test
    void batchHit() {
        int batchSize = 1_000_000;
        int hitCount = 0;
        double rate = 0.5;
        for (int i = 0; i < batchSize; i++) {
            String traceId = UUID.randomUUID().toString();
            if (MurmurHashSamplingCounter.INSTANCE.hit(traceId, rate)) {
                hitCount++;
            }
        }
        System.out.println(hitCount / (double) batchSize);
    }

}
