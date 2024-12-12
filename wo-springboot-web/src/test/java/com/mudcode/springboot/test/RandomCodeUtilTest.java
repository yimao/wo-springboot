package com.mudcode.springboot.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class RandomCodeUtilTest {

    @Test
    public void test() {
        int i = 0;
        while (i < 10) {
            String uuid = UUID.randomUUID().toString();
            String alphanumeric = RandomStringUtils.randomAlphanumeric(16);
            String number = RandomStringUtils.randomNumeric(4);
            System.out.printf("%5d\t%s\t%s\t%s\n", i, uuid, alphanumeric, number);
            i++;
        }
    }

}
