package com.lecturboxd.repository;

import com.lecturboxd.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByLectureId(Long lectureId, Pageable pageable);

    Page<Review> findByUserId(UUID userId, Pageable pageable);

    Optional<Review> findByUserIdAndLectureId(UUID userId, Long lectureId);

    long countByLectureId(Long lectureId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture.id = :lectureId")
    Double findAverageRatingByLectureId(@Param("lectureId") Long lectureId);

    long countByUserId(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
