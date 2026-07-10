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

/**
 * EN: Spring Data repository for Review entities.
 * KA: Spring Data რეპოზიტორი Review ენთითებისთვის.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * EN: Paged reviews for a lecture.
     * KA: ლექციის მიმოხილვები გვერდებად.
     */
    Page<Review> findByLectureId(Long lectureId, Pageable pageable);

    /**
     * EN: Paged reviews written by a user.
     * KA: მომხმარებლის დაწერილი მიმოხილვები გვერდებად.
     */
    Page<Review> findByUserId(UUID userId, Pageable pageable);

    /**
     * EN: Finds the single review for a user+lecture pair if it exists.
     * KA: პოულობს ერთადერთ მიმოხილვას user+lecture წყვილისთვის, თუ არსებობს.
     */
    Optional<Review> findByUserIdAndLectureId(UUID userId, Long lectureId);

    /**
     * EN: Number of reviews on a lecture.
     * KA: ლექციაზე მიმოხილვების რაოდენობა.
     */
    long countByLectureId(Long lectureId);

    /**
     * EN: Average star rating for a lecture (null if no reviews).
     * KA: ლექციის საშუალო ვარსკვლავური რეიტინგი (null თუ მიმოხილვები არ არის).
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture.id = :lectureId")
    Double findAverageRatingByLectureId(@Param("lectureId") Long lectureId);

    /**
     * EN: Number of reviews written by a user.
     * KA: მომხმარებლის მიერ დაწერილი მიმოხილვების რაოდენობა.
     */
    long countByUserId(UUID userId);

    /**
     * EN: Deletes all reviews by a user (account cleanup).
     * KA: შლის მომხმარებლის ყველა მიმოხილვას (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
