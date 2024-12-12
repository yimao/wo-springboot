package com.mudcode.springboot.dao.repo;

import com.mudcode.springboot.ApplicationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
class IdNameRepoTest extends ApplicationTest {

    @Autowired
    private IdNameRepo idNameRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testQuery() {
        List<IdNameEntity> list = idNameRepo.findAll();
        list.forEach(people -> log.info(people.toString()));
    }

    @Test
    void testSave() {
        IdNameEntity p = new IdNameEntity();
        p.setName(UUID.randomUUID().toString());
        p.setStatus(2);
        p = idNameRepo.save(p);
        log.info(p.toString());
    }

    @Test
    void testArrayParameter() {
        List<IdNameEntity> idNameList = this.idNameRepo.findByNames(1, Arrays.asList("a", "b", "c"));
        logger.info(idNameList.toString());
    }

    @Rollback(false)
    @Transactional
    @Test
    void testBatchSave() {
        for (int i = 0; i < 20000; i++) {
            IdNameEntity p = new IdNameEntity();
            p.setName(UUID.randomUUID().toString());
            p.setStatus(2);
            entityManager.persist(p);

            if (i > 0 && i % 20 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

}
