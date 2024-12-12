package com.mudcode.springboot.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

import java.util.Map;
import java.util.UUID;

public class JedisTest {

    private UnifiedJedis jedis;

    @BeforeEach
    public void init() {
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().password("0okm9ijn*UHB").build();
        jedis = new JedisPooled(new HostAndPort("10.1.2.5", 6379), jedisClientConfig);
    }

    @AfterEach
    public void destroy() {
        this.jedis.close();
    }

    @Test
    public void test() {
        int capacity = 10000000;
        String bfName = UUID.randomUUID().toString();
        this.jedis.del(bfName);
        this.jedis.bfReserve(bfName, 1d / capacity, capacity);
        this.jedis.bfAdd(bfName, "abc");
        this.jedis.bfAdd(bfName, "123");
        this.jedis.bfAdd(bfName, "def");
        this.jedis.bfAdd(bfName, "456");
        boolean exists1 = this.jedis.bfExists(bfName, "def");
        Assertions.assertTrue(exists1);
        boolean exists2 = this.jedis.bfExists(bfName, "789");
        Assertions.assertFalse(exists2);
        Map<String, Object> info = this.jedis.bfInfo(bfName);
        System.out.println(info.toString());
        this.jedis.del(bfName);
    }

}
