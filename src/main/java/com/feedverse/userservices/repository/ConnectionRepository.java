package com.feedverse.userservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.feedverse.userservices.model.Connection;
import com.feedverse.userservices.model.DBUser;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Connection c WHERE c.source = :source AND c.target = :target")
    boolean existsBySourceAndTarget(DBUser source, DBUser target);

    @Query("SELECT c FROM Connection c WHERE c.source = :source AND c.target = :target")
    Connection findBySourceAndTarget(@Param("source") DBUser source, @Param("target") DBUser target);

}
