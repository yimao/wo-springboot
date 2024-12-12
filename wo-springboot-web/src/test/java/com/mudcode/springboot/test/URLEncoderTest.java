package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLEncoderTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        String s1 = "V3.6.1.0-%E9%93%B6%E8%81%94.tar.gz.md5";
        String s2 = URLDecoder.decode(s1, "UTF-8");
        System.out.println(s2);
    }

    @Test
    public void testUrl() throws MalformedURLException, URISyntaxException {
        String queryUrl = "http://10.128.2.90:8082/druid/v2?a=1&b=2";
        URL url = new URL(queryUrl);
        System.out.println(url.toURI().toString());
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
        System.out.println(url.getDefaultPort());
        System.out.println(url.getPath());
        System.out.println(url.getQuery());
    }

    @Test
    public void testUrlEncoder() {
        String uri = "migrate/ sqls/dameng/venus_conf_migrate.sql";
        String encodeUri = URLEncoder.encode(uri, StandardCharsets.UTF_8);
        System.out.println(encodeUri);
        System.out.println(encodeUri.replaceAll("\\+", "%20"));
    }

}
