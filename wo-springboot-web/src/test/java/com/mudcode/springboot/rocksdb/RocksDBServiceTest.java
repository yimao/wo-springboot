package com.mudcode.springboot.rocksdb;

import com.mudcode.springboot.ApplicationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

class RocksDBServiceTest extends ApplicationTest {

    @Autowired
    private RocksDBService rocksdbService;

    @Test
    public void testCRUD() throws Exception {
        byte[] key = "abc".getBytes(StandardCharsets.UTF_8);
        byte[] value = "123".getBytes(StandardCharsets.UTF_8);
        this.rocksdbService.save(key, value);
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        Assertions.assertArrayEquals(value, this.rocksdbService.get(key));

        byte[] uuid = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        this.rocksdbService.save(key, uuid);
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        Assertions.assertArrayEquals(uuid, this.rocksdbService.get(key));

        this.rocksdbService.delete(key);
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        Assertions.assertNull(this.rocksdbService.get(key));

        System.out.println(this.rocksdbService.createTimestamp());
        System.out.println(this.rocksdbService.lastUpdateTimestamp());
    }

    @Test
    public void testDel() throws Exception {
        byte[] key = "abcdefg".getBytes(StandardCharsets.UTF_8);
        this.rocksdbService.delete(key);
    }

    @Test
    public void testBatchSave() throws Exception {
        int size = 1_0000;
        for (int i = 0; i < size; i++) {
            byte[] key = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
            byte[] value = String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
            this.rocksdbService.save(key, value);
        }
    }

    @Test
    public void testList() {
        this.rocksdbService.list((k, v) -> logger.info("key: {}, value: {}", new String(k, StandardCharsets.UTF_8),
                new String(v, StandardCharsets.UTF_8)));
    }

}
