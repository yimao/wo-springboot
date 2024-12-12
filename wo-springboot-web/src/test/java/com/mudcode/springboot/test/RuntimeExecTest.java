package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RuntimeExecTest {

    @Test
    public void test() throws IOException, InterruptedException {
        String[] cmd = new String[]{"/bin/sh", "-c", "java -version"};
        Process process = Runtime.getRuntime().exec(cmd);
        int wait = process.waitFor();
        System.out.println("exit code: " + wait);

        try (InputStreamReader reader = new InputStreamReader(process.getInputStream());
             BufferedReader br = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            sb.append("getInputStream():\n");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            System.out.println(result);
        }

        try (InputStreamReader reader = new InputStreamReader(process.getErrorStream());
             BufferedReader br = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            sb.append("getErrorStream():\n");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            System.out.println(result);
        }

    }

}
