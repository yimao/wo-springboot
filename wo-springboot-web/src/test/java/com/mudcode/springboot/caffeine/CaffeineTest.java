package com.mudcode.springboot.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.ExecutorServiceUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CaffeineTest {

    private Cache<Integer, IdNameItem> cache;

    private LoadingCache<Integer, String> cacheLoader;

    @BeforeEach
    public void init() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> System.out
                        .println("Cache RemovalListener, key=" + key + ", value=" + value + ", cause=" + cause.name()))
                .build();
        this.cacheLoader = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .refreshAfterWrite(10, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> System.out
                        .println("LoadingCache RemovalListener, key=" + key + ", value=" + value + ", cause=" + cause.name()))
                .build(new CacheLoader<>() {
                    @Override
                    public @Nullable String load(Integer integer) throws Exception {
                        return UUID.randomUUID().toString();
                    }
                });
    }

    private IdNameItem loadCache(Integer key) {
        IdNameItem student = new IdNameItem();
        student.setName(UUID.randomUUID().toString());
        student.setId(key);
        System.out.println("build cache item: " + student);
        return student;
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        int key = 100;

        IdNameItem student = this.cache.get(key, this::loadCache);
        System.out.println("cache: " + student.hashCode() + " | " + student);

        student.setId(key);

        IdNameItem chg = this.cache.get(key, this::loadCache);
        System.out.println("chg: " + chg.hashCode() + " | " + chg);

        Thread.sleep(15_000);

        IdNameItem reCache = this.cache.get(key, this::loadCache);
        System.out.println("reCache: " + reCache.hashCode() + " | " + reCache);

        Thread.sleep(30_000);
    }

    @Test
    public void testCacheLoader() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            int key = 0; // replace with your actual key
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println(sdf.format(System.currentTimeMillis()) + ": " + this.cacheLoader.get(key));
        }, 0, 1, TimeUnit.SECONDS);

        // Wait for 5 minutes
        Thread.sleep(TimeUnit.MINUTES.toMillis(2));

        // Shutdown the executor service
        executorService.shutdown();
    }

    @Test
    public void testCacheExpire() throws InterruptedException {
        ScheduledExecutorService executorService = ExecutorServiceUtil.newSingleThreadScheduledExecutor("cache");
        int key = 1;

        executorService.scheduleAtFixedRate(() -> {
            IdNameItem cacheItem = this.cache.get(key, k -> loadCache(key));
            System.out.println("match cache item: " + cacheItem);
        }, 0, 1, TimeUnit.SECONDS);

        // Wait for a while to see the cache expiration in action
        Thread.sleep(TimeUnit.SECONDS.toMillis(120));

        // Shutdown the executor service
        ExecutorServiceUtil.stop(executorService);
    }

}
