package com.mudcode.springboot.queue;

import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.ExecutorServiceUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ArrayBlockingQueueTest {

    private static final Logger logger = LoggerFactory.getLogger(ArrayBlockingQueueTest.class);

    // 本地数据队列，固定大小
    private BlockingQueue<IdNameItem> blockingQueue;

    // 批量处理数据线程池，固定并发度
    private ThreadPoolExecutor flushExecutorService;

    // 定时任务，定时触发数据线程池处理数据，固定周期
    private ScheduledExecutorService scheduledExecutorService;

    @BeforeEach
    public void init() {
        // init local queue
        int blockingQueueCapacity = 20000;
        this.blockingQueue = new ArrayBlockingQueue<>(blockingQueueCapacity);

        // init flush data thread pool
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.flushExecutorService = ExecutorServiceUtil.newThreadPoolExecutor(availableProcessors, availableProcessors, "data-flush");

        // init scheduled thread pool
        this.scheduledExecutorService = ExecutorServiceUtil.newSingleThreadScheduledExecutor("data-flush-trigger");
        this.scheduledExecutorService.scheduleWithFixedDelay(this::flushLocalQueue, 1, 1, TimeUnit.SECONDS);
    }

    @AfterEach
    public void destroy() {
        // flush data
        this.flushLocalQueue();

        ExecutorServiceUtil.stop(scheduledExecutorService, 10);
        ExecutorServiceUtil.stop(flushExecutorService, 10);
    }

    private void flushLocalQueue() {
        if (this.blockingQueue.isEmpty()) {
            return;
        }

        List<IdNameItem> idNameItems = new ArrayList<>();
        this.blockingQueue.drainTo(idNameItems);

        idNameItems.forEach(idNameItem -> {
            // flush data to db
            logger.info("flushIdQueue: {}", idNameItem.toString());
        });
    }

    public void addIdNameItem(IdNameItem item) {
        while (!this.blockingQueue.offer(item)) {
            this.flushExecutorService.submit(this::flushLocalQueue);
        }
    }

    @Test
    void testAddIdNameItem() {
        IdNameItem item1 = new IdNameItem();
        this.addIdNameItem(item1);
    }

}
