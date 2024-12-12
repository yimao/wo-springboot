package com.mudcode.springboot.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrentHashMapTest {

    private static final Logger logger = LoggerFactory.getLogger(CurrentHashMapTest.class);

    private ScheduledExecutorService executorService;

    @BeforeEach
    public void before() {
        executorService = Executors.newScheduledThreadPool(2);
    }

    @AfterEach
    public void after() throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(60));
        executorService.shutdownNow();
    }

    @Test
    public void testConcurrentHashMap() throws InterruptedException {
        // Map<String, String> map = new HashMap<>();
        Map<String, String> map = new ConcurrentHashMap<>();

        executorService.scheduleAtFixedRate(() -> {
            try {
                logger.info("map size:{}", map.size());
                map.forEach((k, v) -> logger.info(k));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(() -> {
            map.clear();
            int size = 10;
            while (size-- > 0) {
                String uuid = UUID.randomUUID().toString();
                map.put(uuid, uuid);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testConcurrentList() {
        // List<String> list = new ArrayList<>();
        List<String> list = new CopyOnWriteArrayList<>();

        executorService.scheduleAtFixedRate(() -> {
            try {
                logger.info("list size:{}", list.size());
                list.forEach(logger::info);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(() -> {
            list.clear();
            int size = 10;
            while (size-- > 0) {
                String uuid = UUID.randomUUID().toString();
                list.add(uuid);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testConcurrentSet() {
        // Set<String> list = new HashSet<>();
        Set<String> list = new ConcurrentSkipListSet<>();

        executorService.scheduleAtFixedRate(() -> {
            try {
                logger.info("list size:{}", list.size());
                list.forEach(logger::info);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(() -> {
            list.clear();
            int size = 10;
            while (size-- > 0) {
                String uuid = UUID.randomUUID().toString();
                list.add(uuid);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
