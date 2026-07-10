package com.lecturboxd.repository;

import com.lecturboxd.entity.LectureLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LectureLogRepository extends JpaRepository<LectureLog, Long> {

    Page<LectureLog> findByUserIdOrderByWatchedAtDescCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<LectureLog> findByUserIdAndLectureId(UUID userId, Long lectureId);

    long countByUserId(UUID userId);

    boolean existsByUserIdAndLectureId(UUID userId, Long lectureId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM LectureLog l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
