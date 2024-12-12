package com.mudcode.springboot.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentListTest {

    // v1
    private final List<String> list_v1 = new ArrayList<>();

    // v2
    private volatile List<String> list_v2 = Collections.emptyList();

    public static void main(String[] args) {
        ConcurrentListTest concurrentList = new ConcurrentListTest();
        // concurrentCode.executorV1();
        concurrentList.executorV2();
    }

    // v1
    public synchronized void writeV1() {
        List<String> temp = getList();
        list_v1.clear();
        list_v1.addAll(temp);
        System.out.println("v1 write thread:" + Thread.currentThread().getName() + ", list size:" + list_v1.size());
    }

    // v1
    public synchronized List<String> readV1() {
        return list_v1;
    }

    // v2
    public synchronized void writeV2() {
        List<String> temp = getList();
        list_v2 = Collections.emptyList();
        list_v2 = temp;
        System.out.println("v2 write thread:" + Thread.currentThread().getName() + ", list size:" + list_v2.size());
    }

    // v2
    public List<String> readV2() {
        return list_v2;
    }

    private List<String> getList() {
        List<String> temp = new ArrayList<>();
        Random rand = new Random();
        int listSize = rand.nextInt(100);
        for (int i = 0; i < listSize; i++) {
            final int minLength = 512;
            final int maxLength = 1024;
            StringBuilder message = new StringBuilder(Thread.currentThread().getName() + ":");
            int randomMessageLength = rand.nextInt(maxLength - minLength) + minLength;
            for (int length = message.length(); length < randomMessageLength; ++length) {
                message.append('x');
            }
            temp.add(message.toString());
        }
        return temp;
    }

    // v1
    private void executorV1() {
        this.writeV1();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::writeV1, 0, 5, TimeUnit.SECONDS);
        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    for (String str : this.readV1()) {
                        int listSize = this.readV1().size();
                        System.out.println("v1 read thread:" + Thread.currentThread().getName() + ", list size:"
                                + listSize + ", str size:" + str.length());
                        try {
                            Thread.sleep(1000 * 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("v1:" + Thread.currentThread().getName() + ":" + e.getClass().getName());
                    // e.printStackTrace();
                }
            }
        });
    }

    // v2
    private void executorV2() {
        this.writeV2();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::writeV2, 0, 5, TimeUnit.SECONDS);
        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    for (String str : this.readV2()) {
                        int listSize = this.readV2().size();
                        System.out.println("v2 read thread:" + Thread.currentThread().getName() + ", list size:"
                                + listSize + ", str size:" + str.length());
                        try {
                            Thread.sleep(1000 * 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("v2:" + Thread.currentThread().getName() + ":" + e.getClass().getName());
                    // e.printStackTrace();
                }
            }
        });
    }

}
