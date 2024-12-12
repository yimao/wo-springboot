package com.mudcode.springboot.common.encoder;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

class ByteEncoderTest {

    @Test
    public void testBytes() {
        int i = 1234567890;

        byte[] bufferBytes = ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
        System.out.println("bufferBytes: " + Arrays.toString(bufferBytes));

        BigInteger bigInteger = BigInteger.valueOf(i);
        byte[] bigBytes = bigInteger.toByteArray();
        Assertions.assertArrayEquals(bufferBytes, bigBytes);

        byte[] intsBytes = Ints.toByteArray(i);
        Assertions.assertArrayEquals(bufferBytes, intsBytes);

        byte[] byteEncoder = ByteEncoder.toByteArray(i);
        Assertions.assertArrayEquals(bufferBytes, byteEncoder);
    }

    @Test
    public void testLongBytes() {
        long l = 1234567890L;

        byte[] bufferBytes = ByteBuffer.allocate(Long.BYTES).putLong(l).array();
        System.out.println("bufferBytes: " + Arrays.toString(bufferBytes));

        byte[] longsBytes = Longs.toByteArray(l);
        Assertions.assertArrayEquals(bufferBytes, longsBytes);

        byte[] byteEncoder = ByteEncoder.toByteArray(l);
        Assertions.assertArrayEquals(bufferBytes, byteEncoder);
    }

}
