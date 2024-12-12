package com.mudcode.springboot.test;

import com.mudcode.springboot.common.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesTest {

    @Test
    public void test() throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = this.getClass().getResourceAsStream("/application-test.properties")) {
            properties.load(stream);
        }
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + ": " + properties.get(key));
        }
    }

    @Test
    public void test1() throws IOException {
        Properties properties = new Properties();
        PropertiesUtil.load("classpath:/application-test.properties", properties);
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + ": " + properties.get(key));
        }
    }

    @Test
    public void test2() throws IOException {
        URL url = this.getClass().getResource("/application-test.properties");
        String filePath = "file:" + url.getFile();
        System.out.println(filePath);

        Properties properties = new Properties();
        PropertiesUtil.load(filePath, properties);

        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + ": " + properties.get(key));
        }
    }

}
