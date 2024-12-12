package com.mudcode.springboot.test;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.digest.MurmurHash2;
import org.apache.commons.codec.digest.MurmurHash3;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

public class MurmurHashCodeTest {

    @Test
    public void test() {
        byte[] bytes = new byte[1024];
        Random random = new Random();
        random.nextBytes(bytes);

        // MurmurHash2
        long hash64 = MurmurHash2.hash64(bytes, bytes.length);
        System.out.println("MurmurHash2.hash64(): " + hash64);

        // MurmurHash3
        long[] hash128 = MurmurHash3.hash128x64(bytes);
        System.out.println("MurmurHash3.hash128x64(): " + Arrays.toString(hash128));

        // google guava hashing hash128
        System.out.println("Hashing.murmur3_128(): " + Hashing.murmur3_128().hashBytes(bytes).asLong());

    }

}
