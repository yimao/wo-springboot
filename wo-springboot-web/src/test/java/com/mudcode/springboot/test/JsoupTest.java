package com.mudcode.springboot.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class JsoupTest {

    @Test
    public void test() throws IOException {
        // String url = "https://jsoup.org/";
        String url = "https://api.ctlcode.com/index.html";
        Document jsoup = Jsoup.parse(new URL(url), 10_000);
        System.out.println(jsoup.title());
    }

}
