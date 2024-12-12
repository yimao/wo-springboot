package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    private static final Logger logger = LoggerFactory.getLogger(RegexTest.class);

    @Test
    public void test() {
        Pattern pattern = Pattern.compile("^(http|https)://([a-zA-Z0-9.]+)(/.*)$");

        String origin = "https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%8D%8E";

        Matcher matcher = pattern.matcher(origin);
        logger.info("matches: {}", matcher.matches());

        if (matcher.matches()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                logger.info("matcher: {} -> {}", i, matcher.group(i));
            }
        }
    }

    @Test
    public void test2() {
        Pattern pattern = Pattern.compile("^/sales/.+$");
        String checkStr = "/sales/order/detail";
        System.out.println(pattern.matcher(checkStr).matches());
    }

}
