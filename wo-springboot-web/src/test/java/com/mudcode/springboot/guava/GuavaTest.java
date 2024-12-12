package com.mudcode.springboot.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mudcode.springboot.bean.IdNameItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaTest {

    private LoadingCache<Integer, IdNameItem> loadingCache;

    @BeforeEach
    public void init() {
        this.loadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .removalListener(
                        removalNotification -> System.out.println("RemovalCause: " + removalNotification.getCause().name()))
                .build(new CacheLoader<>() {
                    @Override
                    public IdNameItem load(Integer integer) throws Exception {
                        IdNameItem student = new IdNameItem();
                        student.setName(UUID.randomUUID().toString());
                        student.setId(new Random().nextInt(integer));
                        System.out.println("loading: " + student.hashCode() + " | " + student);
                        return student;
                    }
                });
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        int key = 100;

        IdNameItem student = this.loadingCache.get(key);
        System.out.println("cache: " + student.hashCode() + " | " + student);

        student.setId(key);

        IdNameItem chg = this.loadingCache.get(key);
        System.out.println("chg: " + chg.hashCode() + " | " + chg);

        Thread.sleep(15_000);

        IdNameItem reCache = this.loadingCache.get(key);
        System.out.println("reCache: " + reCache.hashCode() + " | " + reCache);

        Thread.sleep(30_000);
    }

}
