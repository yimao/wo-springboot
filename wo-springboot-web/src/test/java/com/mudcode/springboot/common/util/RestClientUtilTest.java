package com.mudcode.springboot.common.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestClientUtilTest {

    @Test
    public void testGet() {
        String url = "https://api.ctlcode.com/";
        String response = RestClientUtil.get(url, null);
        System.out.println(response);
        assertNotNull(response);
    }

    @Test
    public void testPost() {
        String url = "https://api.ctlcode.com/webhook";
        String response = RestClientUtil.postForm(url, Map.of("p1", "v1"));
        System.out.println(response);
        assertNotNull(response);
    }

}
