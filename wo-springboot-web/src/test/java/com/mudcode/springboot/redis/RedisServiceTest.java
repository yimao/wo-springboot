package com.mudcode.springboot.redis;

import com.mudcode.springboot.ApplicationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

class RedisServiceTest extends ApplicationTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        int index = 1000;
        long t0 = System.currentTimeMillis();
        while (index > 0) {
            String test = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(test, test);
            String check = stringRedisTemplate.opsForValue().get(test);
            Assertions.assertEquals(test, check);
            logger.debug("{}: {}", test, index--);
        }
        long t1 = System.currentTimeMillis();
        logger.info("duration: {}ms", t1 - t0);
    }

    @Test
    void testBatch() {
        int index = 100_000;
        long t0 = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        while (index > 0) {
            String test = UUID.randomUUID().toString();
            map.put(test, test);
            index--;
        }
        stringRedisTemplate.opsForValue().multiSet(map);
        long t1 = System.currentTimeMillis();
        logger.info("duration: {}ms", t1 - t0);
    }

    @Test
    void testMget() {
        Map<String, String> values = new HashMap<>();
        values.put("k1", "v1");
        values.put("k2", "v2");
        values.put("k3", "0");
        stringRedisTemplate.opsForValue().multiSet(values);
        Objects.requireNonNull(stringRedisTemplate.opsForValue().multiGet(values.keySet()))
                .forEach(System.out::println);
    }

}
