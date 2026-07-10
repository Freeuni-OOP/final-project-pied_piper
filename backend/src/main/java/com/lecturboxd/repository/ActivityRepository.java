package com.lecturboxd.repository;

import com.lecturboxd.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE a.user.id IN :userIds ORDER BY a.createdAt DESC")
    Page<Activity> findByUserIdIn(@Param("userIds") Collection<UUID> userIds, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Activity a WHERE a.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
