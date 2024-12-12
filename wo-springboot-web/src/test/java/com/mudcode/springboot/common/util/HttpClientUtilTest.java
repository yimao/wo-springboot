package com.mudcode.springboot.common.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpClientUtilTest {

    private HttpClientUtil httpClientUtil;

    @BeforeEach
    public void beforeTest() throws Exception {
        httpClientUtil = new HttpClientUtil("admin", "0okm9ijn*UHB");
        httpClientUtil.init();
    }

    @AfterEach
    public void afterTest() {
        httpClientUtil.close();
    }

    @Test
    public void get() throws Exception {
        String url = "https://api.ctlcode.com/";
        String get = httpClientUtil.get(url, null, null);
        System.out.println(get);
    }

    @Test
    public void testKeepAlive() throws IOException {
        HttpGet httpGet = new HttpGet("https://api.ctlcode.com/");
        CloseableHttpResponse response = this.httpClientUtil.execute(httpGet);
        System.out.println(EntityUtils.toString(response.getEntity()));
        response.close();
        httpGet = new HttpGet("https://api.ctlcode.com/randomBytes");
        response = this.httpClientUtil.execute(httpGet);
        System.out.println(EntityUtils.toString(response.getEntity()));
        response.close();
    }

}
