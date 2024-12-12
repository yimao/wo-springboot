package com.mudcode.springboot.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    @AfterEach
    public void after() {
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("scheduleAtFixedRate: " + new Date());
            }
        }, 0, 2 * 1000 * 10);
    }

    @Test
    public void test2() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("scheduleAtFixedRate: " + new Date());
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Test
    public void test3() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            System.out.println("scheduleWithFixedDelay: " + new Date());
            // throw new RuntimeException();
        }, 0, 10, TimeUnit.SECONDS);
    }

}
