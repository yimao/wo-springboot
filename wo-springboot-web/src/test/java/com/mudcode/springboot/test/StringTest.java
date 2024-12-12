package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringTest {

    @Test
    public void testSplit() {
        String str = "a:b:c:d:e";
        System.out.println(Arrays.toString(str.split(":")));
        System.out.println(Arrays.toString(str.split(":", 2)));
        System.out.println(Arrays.toString(str.split("#", 2)));
    }

    @Test
    public void testSubString() {
        String str = "abc/def.txt";
        System.out.println(str.substring(str.lastIndexOf("/")));
        System.out.println(str.substring(str.lastIndexOf("/") + 1));

        String contextPath = "/v0";
        String uri = "/v0/webhook";
        System.out.println(uri.substring(contextPath.length()));

        String methodName = "lambda$syncSessionToRedis$0";
        System.out.println(methodName);
        String methodName1 = methodName.substring("lambda$".length());
        System.out.println(methodName1);
        String methodName2 = methodName.substring(0, methodName.indexOf("$"));
        System.out.println(methodName2);
    }

    @Test
    public void testOrder() {
        List<String> list = new ArrayList<>();
        list.add("com.tingyun.bpi:bpi-adhoc-common:3.6.1.0");
        list.add("com.tingyun.bpi:bpi-adhoc-common:3.6.1.2");
        list.add("com.tingyun.bpi:bpi-adhoc-common:3.6.1.1");
        list.add("com.tingyun.bpi:bpi-adhoc-common:3.6.1.3");

        System.out.println("0=== === ===");
        list.forEach(System.out::println);
        System.out.println("1=== === ===");
        list.stream().sorted(String::compareToIgnoreCase).forEach(System.out::println);
        System.out.println("2=== === ===");
        list.stream().sorted(String::compareToIgnoreCase).forEachOrdered(System.out::println);
        System.out.println("3=== === ===");
        list.stream().sorted(String.CASE_INSENSITIVE_ORDER.reversed()).forEachOrdered(System.out::println);
    }

    private String trimHashMark(String line) {
        if (line.startsWith("#")) {
            line = line.substring(1);
        } else if (line.startsWith("--")) {
            line = line.substring(2);
        } else if (line.startsWith("/*") && line.endsWith("*/")) {
            line = line.substring(2, line.length() - 2);
        }
        return line;
    }

    @Test
    public void testTrimHashMark() {
        String[] strs = new String[]{"# 12  3", "-- alert xx    xxxx ", "/* deld    eldel zz            zzz */"};
        Arrays.stream(strs).forEach(str -> {
            str = str.replaceAll("\\r|\\n|\\t|\\s", "");
            System.out.println(trimHashMark(str));
        });
    }

    @Test
    public void testReplace() {
        String str = "com.tingyun.bpi:bpi-adhoc-common:3.6.1.0";
        System.out.println(str);
        System.out.println(str.replace(":", "-"));
        System.out.println(str.replaceAll(":", "-"));
    }

}
