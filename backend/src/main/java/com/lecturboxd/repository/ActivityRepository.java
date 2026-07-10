package com.lecturboxd.repository;

import com.lecturboxd.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @EntityGraph(attributePaths = {"user", "lecture", "review"})
    @Query("SELECT a FROM Activity a WHERE a.user.id IN :userIds ORDER BY a.createdAt DESC")
    Page<Activity> findByUserIdIn(@Param("userIds") Collection<UUID> userIds, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Activity a WHERE a.user.id = :userId OR a.review.id IN (SELECT r.id FROM Review r WHERE r.user.id = :userId)")
    void deleteByUserId(@Param("userId") UUID userId);
}
