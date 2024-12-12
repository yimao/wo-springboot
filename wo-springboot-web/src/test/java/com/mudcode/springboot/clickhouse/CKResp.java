package com.mudcode.springboot.clickhouse;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CKResp {

    private int status;

    private String message;

    private String endpoint;

    public boolean successes() {
        return status == 200;
    }

}
