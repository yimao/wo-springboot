package com.mudcode.springboot.dao;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.dao.repo.IdNameEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class JdbcTest extends ApplicationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void queryForMap() {
        Map<String, Object> map = this.jdbcTemplate.queryForMap(
                "SELECT unix_timestamp() AS 'timestamp', now() as 'now', 'Hello, World!' as 'en', '你好，世界！' as 'cn'");
        logger.info(map.toString());
    }

    @Test
    void queryForString() {
        String str = this.jdbcTemplate.queryForObject("SELECT now()", String.class);
        logger.info(str);
    }

    @Test
    void queryListObject() {
        List<IdNameEntity> idNameList = this.jdbcTemplate.query("SELECT * from id_name",
                new BeanPropertyRowMapper<>(IdNameEntity.class));
        logger.info(idNameList.toString());
    }

    @Test
    void testArrayParameter() {
        String sql = "select * from id_name where status = :status and name in (:names)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("status", 2);
        sqlParameterSource.addValue("names", Arrays.asList("f675381d", "b287", "41a9", "b3ea", "01296c8f0c42"));
        List<IdNameEntity> idNameList = namedParameterJdbcTemplate.query(sql, sqlParameterSource,
                new BeanPropertyRowMapper<>(IdNameEntity.class));
        logger.info(idNameList.toString());
    }

    @Test
    void testBatchInsert() {
        String sql = "insert into id_name (name, status) values (?, ?)";
        this.jdbcTemplate.batchUpdate(sql, Arrays.asList(new Object[][]{{"f675381d", 2}, {"b287", 2},
                {"41a9", 2}, {"b3ea", 2}, {"01296c8f0c42", 2}}));
    }

    @Test
    void testBatchUpdate() {
        int BATCH_SIZE = 1000; // 你可以根据你的应用程序和数据库特性调整这个值

        List<IdNameEntity> entities = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            IdNameEntity entity = new IdNameEntity();
            entity.setName(UUID.randomUUID().toString());
            entity.setStatus(i % 2);
            entities.add(entity);
        }

        String sql = "insert into id_name (name, status) values (?, ?)";

        int batchCount = (entities.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        for (int i = 0; i < batchCount; i++) {
            final List<IdNameEntity> batchList = entities.subList(i * BATCH_SIZE,
                    Math.min((i + 1) * BATCH_SIZE, entities.size()));
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int j) throws SQLException {
                    IdNameEntity entity = batchList.get(j);
                    preparedStatement.setString(1, entity.getName());
                    preparedStatement.setInt(2, entity.getStatus());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }

}
