package com.mudcode.springboot.sampling;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class MurmurHashSamplingCounter {

    public static final MurmurHashSamplingCounter INSTANCE = new MurmurHashSamplingCounter();

    private MurmurHashSamplingCounter() {
    }

    /**
     * 计算是否命中
     *
     * @param probability 概率值
     * @return true 命中，false 未命中
     */
    public boolean hit(String traceId, double probability) {
        if (probability < 0d || traceId == null) {
            return false;
        }
        int hash = Hashing.murmur3_32_fixed().hashBytes(traceId.getBytes(StandardCharsets.UTF_8)).asInt();
        int t = (int) (Integer.MAX_VALUE * probability);
        return hash >= -t && hash <= t;
    }

}
