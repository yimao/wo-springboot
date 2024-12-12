package com.mudcode.springboot.dao.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdNameRepo extends JpaRepository<IdNameEntity, Integer> {

    @Query("select e from IdNameEntity e WHERE e.status = ?1 AND e.name  IN ?2")
    public List<IdNameEntity> findByNames(int status, List<String> names);

}
