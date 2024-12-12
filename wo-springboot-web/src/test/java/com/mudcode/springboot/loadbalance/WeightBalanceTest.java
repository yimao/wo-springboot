package com.mudcode.springboot.loadbalance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class WeightBalanceTest {

    @Test
    public void testNext() {
        WeightBalance<String> weightBalance = WeightBalance.create();
        weightBalance.add("A", 1);
        weightBalance.add("B", 3);
        weightBalance.add("C", 4);

        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("A", 0);
        countMap.put("B", 0);
        countMap.put("C", 0);

        int total = 10000;
        for (int i = 0; i < total; i++) {
            String next = weightBalance.next();
            countMap.put(next, countMap.get(next) + 1);
        }

        double aRatio = countMap.get("A") / (double) total;
        double bRatio = countMap.get("B") / (double) total;
        double cRatio = countMap.get("C") / (double) total;

        Assertions.assertTrue(Math.abs(aRatio - 1.0 / 8) < 0.01);
        Assertions.assertTrue(Math.abs(bRatio - 3.0 / 8) < 0.01);
        Assertions.assertTrue(Math.abs(cRatio - 4.0 / 8) < 0.01);
    }

}
