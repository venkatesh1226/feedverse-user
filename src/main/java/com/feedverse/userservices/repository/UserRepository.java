package com.feedverse.userservices.repository;

import com.feedverse.userservices.model.DBUser;

import jakarta.transaction.Transactional;

import com.feedverse.userservices.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<DBUser, String> {
        @Query("SELECT u FROM DBUser u WHERE u.username = ?1")
        DBUser findByUsername(String username);

        @Query("SELECT c FROM Connection conn1 " +
                        "JOIN conn1.target b " +
                        "JOIN Connection conn2 ON conn2.source = b " +
                        "JOIN conn2.target c " +
                        "WHERE conn1.source.username = ?1 AND c.username NOT IN " +
                        "(SELECT conn3.target.username FROM Connection conn3 WHERE conn3.source.username = ?1)")
        Set<DBUser> findNonMutualFollowers(String username);

        @Query("SELECT u FROM DBUser u WHERE u.username LIKE '%' || ?1 || '%' OR u.name LIKE '%' || ?1 || '%'")
        Set<DBUser> findUsers(String searchParam);

        @Query("SELECT u FROM DBUser u WHERE u.username = ?1 OR u.email = ?1")
        Set<DBUser> findUsersByExactUsernameOrEmail(String searchParam);

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO Connection (source_user_id, target_user_id, since) VALUES (:username1, :username2, CURRENT_DATE)", nativeQuery = true)
        void followUser(String username1, String username2);

        @Query("SELECT c FROM Connection c WHERE c.target.username = ?1")
        Set<Connection> findFollowers(String username);

        @Query("SELECT c FROM Connection c WHERE c.source.username = ?1")
        Set<Connection> findFollowing(String username);

        @Transactional
        @Modifying
        @Query("DELETE FROM Connection c WHERE c.source.username = ?1 AND c.target.username = ?2")
        void unfollowUser(String username, String followingUsername);

}
