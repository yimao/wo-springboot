package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

public class PathMatcherTest {

    @Test
    public void test() {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:neuralert-dc-error.*.log");
        System.out.println(matcher.matches(Paths.get("neuralert-dc-error.2023-02-08.10.log")));
        System.out.println(matcher.matches(Paths.get("neuralert-dc-error.2023-02-08.log")));
        System.out.println(matcher.matches(Paths.get("neuralert-dc-error.1.log")));
    }

}
