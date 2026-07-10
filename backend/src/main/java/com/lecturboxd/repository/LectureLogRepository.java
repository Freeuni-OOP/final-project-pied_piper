package com.lecturboxd.repository;

import com.lecturboxd.entity.LectureLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LectureLogRepository extends JpaRepository<LectureLog, Long> {

    Page<LectureLog> findByUserIdOrderByWatchedAtDescCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<LectureLog> findByUserIdAndLectureId(UUID userId, Long lectureId);

    long countByUserId(UUID userId);

    boolean existsByUserIdAndLectureId(UUID userId, Long lectureId);
}
