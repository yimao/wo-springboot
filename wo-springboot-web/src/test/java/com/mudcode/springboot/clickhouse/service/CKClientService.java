package com.mudcode.springboot.clickhouse.service;

import com.mudcode.springboot.clickhouse.CKResp;

import java.util.List;

public interface CKClientService {

    public CKResp insert(String sql);

    public CKResp query(String sql);

    public List<CKResp> ddl(String sql);

}
