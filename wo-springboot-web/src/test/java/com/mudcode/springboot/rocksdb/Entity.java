package com.mudcode.springboot.rocksdb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity {

    private Operation operation;

    private byte[] key;

    private byte[] value;

    public enum Operation {

        PUT, DEL, GET

    }

}
