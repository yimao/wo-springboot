package com.mudcode.springboot.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import java.util.Base64;
import java.util.Random;

public class AntPathMatcherTest {

    @Test
    public void test() {
        AntPathMatcher matcher = new AntPathMatcher();
        Random random = new Random();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        String url = Base64.getUrlEncoder().encodeToString(bytes);

        Assertions.assertFalse(matcher.match("/url", "/urlNotMatch"));
        Assertions.assertFalse(matcher.match("/url", "/url/" + url));
        Assertions.assertTrue(matcher.match("/url/*", "/url/" + url));
        Assertions.assertTrue(matcher.match("/url/*", "/url/" + url + "?token=123"));
        Assertions.assertTrue(matcher.match("/url/*", "/url/" + url + "#token=123"));
        Assertions.assertFalse(matcher.match("/url/*", "/url/" + url + "/others"));
        Assertions.assertTrue(matcher.match("/url/**", "/url/" + url + "/others"));
        Assertions.assertTrue(matcher.match("/url/*/*", "/url/" + url + "/others"));
        Assertions.assertFalse(matcher.match("/url**", "/url/" + url + "/others"));

        Assertions.assertTrue(matcher.match("dubbo-2.7.15.jar", "dubbo-2.7.15.jar"));
        Assertions.assertFalse(matcher.match("dubbo-2.7.15.jar.bak", "dubbo-2.7.15.jar"));
        Assertions.assertFalse(matcher.match("dubbo-2.7.15.jar", "dubbo-2.7.15.jar.bak"));
        Assertions.assertFalse(matcher.match("dubbo-2.7.15.jar", "dubbo-2.7.20.jar"));
        Assertions.assertTrue(matcher.match("*dubbo-2*.jar", "dubbo-2.7.19.jar"));

        Assertions.assertFalse(matcher.match("/actuator/**", "/s/..;/actuator/health"));
        Assertions.assertTrue(matcher.match("/**/actuator/**", "/s/..;/actuator/health"));
    }

}
