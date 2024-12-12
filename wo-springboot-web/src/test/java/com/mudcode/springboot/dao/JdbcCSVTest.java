package com.mudcode.springboot.dao;

import com.mudcode.springboot.ApplicationTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JdbcCSVTest extends ApplicationTest {

    private final String customer = "同方人寿";

    private final String filePath = "/Users/yimao/Downloads/" + customer;

    private final File agentVersionFile = new File(filePath + "/" + "wukong-agent-version-stat.csv");

    private final File libVersionFile = new File(filePath + "/" + "wukong-app-lib-version-stat.csv");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testAgentVersion() throws IOException {
        try (CSVParser parser = CSVParser.parse(agentVersionFile, StandardCharsets.UTF_8, CSVFormat.RFC4180)) {
            String sql = "insert into agent_version (`product`, `agent`, `count`, `customer`) values (?,?,?,?)";
            for (CSVRecord csvRecord : parser) {
                if (csvRecord.size() != 3) {
                    logger.error(csvRecord.toString());
                    continue;
                }
                String product = csvRecord.get(0);
                String agent = csvRecord.get(1);
                String count = csvRecord.get(2);
                try {
                    this.jdbcTemplate.update(sql, product, agent, count, customer);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    @Test
    public void testAgentLIBVersion() throws IOException {
        try (CSVParser parser = CSVParser.parse(libVersionFile, StandardCharsets.UTF_8, CSVFormat.RFC4180)) {
            int batchSize = 1000;
            List<Object[]> batchParams = new ArrayList<>();
            String sql = "insert into agent_lib_version (`lib_name`, `lib_version`, `count`, `customer`) values (?,?,?,?)";
            for (CSVRecord csvRecord : parser) {
                if (csvRecord.size() != 3) {
                    logger.error(csvRecord.toString());
                    continue;
                }
                String libName = csvRecord.get(0);
                String libVersion = csvRecord.get(1);
                String count = csvRecord.get(2);

                batchParams.add(new Object[]{libName, libVersion, count, customer});

                if (batchParams.size() > batchSize) {
                    try {
                        this.jdbcTemplate.batchUpdate(sql, batchParams);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    batchParams.clear();
                }
            }
            if (batchParams.size() > 0) {
                try {
                    this.jdbcTemplate.batchUpdate(sql, batchParams);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                batchParams.clear();
            }
        }
    }

}
