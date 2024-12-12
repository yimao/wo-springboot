package com.mudcode.springboot.rocksdb;

import com.mudcode.springboot.common.util.JsonUtil;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public final class Metadata {

    public static final byte[] DB_KEY = "__metadata".getBytes(StandardCharsets.UTF_8);

    // 创建时间
    private long createTimestamp;

    // 最新的数据更新时间
    private long lastUpdateTimestamp;

    public static Metadata parseFrom(byte[] bytes) {
        return JsonUtil.toObject(bytes, Metadata.class);
    }

    public static byte[] toByteArray(Metadata metadata) {
        return JsonUtil.toJsonBytes(metadata);
    }

}
