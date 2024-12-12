package com.mudcode.springboot.guava;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuavaBloomFilterTest {

    private final int expectedInsertions = 100_0000;

    private BloomFilter<String> bloomFilter;

    @BeforeEach
    public void init() {
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedInsertions, (double) 1 / 1_0000);
    }

    @Test
    public void testBloomFilter() {
        List<String> list = new ArrayList<>(expectedInsertions);

        for (int i = 0; i < expectedInsertions; i++) {
            String uuid = UUID.randomUUID().toString();
            bloomFilter.put(uuid);
            list.add(uuid);
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);

        int mightContainNum = 0;
        for (int i = 0; i < expectedInsertions; i++) {
            String key = list.get(i);
            if (bloomFilter.mightContain(key)) {
                mightContainNum++;
            }
        }
        System.out.println("【key存在的情况】布隆过滤器的识别率：" + percentFormat.format((float) mightContainNum / expectedInsertions));

        mightContainNum = 0;
        for (int i = 0; i < expectedInsertions; i++) {
            String key = UUID.randomUUID().toString();
            if (bloomFilter.mightContain(key)) {
                mightContainNum++;
            }
        }
        System.out
                .println("【key不存在的情况】布隆过滤器的误判率：" + percentFormat.format((float) mightContainNum / expectedInsertions));

    }

}
