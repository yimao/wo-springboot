package com.mudcode.springboot.es.test;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.JsonUtil;
import com.mudcode.springboot.es.ESIndexRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

class ESIndexRequestServiceTest extends ApplicationTest {

    @Autowired
    private ESIndexRequestService indexService;

    @Test
    void indexRequest() {
        String indexName = "id_name_20231102";

        int size = 1000;
        while (size > 0) {
            size--;
            IdNameItem item = randomItem();
            indexService.indexRequest(indexName, String.valueOf(item.getId()), JsonUtil.toJson(item));
        }
    }

    private IdNameItem randomItem() {
        IdNameItem item = new IdNameItem();
        item.setId(System.currentTimeMillis());
        item.setName(UUID.randomUUID().toString());
        item.setDateTime(new Date());
        return item;
    }

}
