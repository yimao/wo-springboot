package com.mudcode.springboot.clickhouse;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.clickhouse.service.CKClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CKServiceTest extends ApplicationTest {

    @Autowired
    private CKClientService clientService;

    @Test
    void testDDL() {
        logger.info("{}",
                this.clientService.ddl("CREATE TABLE IF NOT EXISTS id_name (id Int64, name String) ENGINE = Memory"));
    }

    @Test
    void testQuery() {
        logger.info("{}", this.clientService.query("select * from id_name"));
    }

    @Test
    void testInsert() {
        logger.info("{}", this.clientService.query("insert into id_name values (1, 'mudcode')"));
    }

}
