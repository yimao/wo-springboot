package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class CRCTest {

    @Test
    public void test() {
        CRC32 crc = new CRC32();
        crc.update(ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8)));
        System.out.println(crc.getValue());
    }

}
