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

/**
 * EN: Spring Data repository for feed Activity entities.
 * KA: Spring Data რეპოზიტორი ფიდის Activity ენთითებისთვის.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * EN: Paged activities for a set of user IDs (e.g. followed users), newest first, with user/lecture/review loaded.
     * KA: გვერდებად დაყოფილი აქტივობები მომხმარებლის ID-ების ნაკრებისთვის (მაგ. გამოწერილები), უახლესი პირველი, user/lecture/review ჩატვირთული.
     */
    @EntityGraph(attributePaths = {"user", "lecture", "review"})
    @Query("SELECT a FROM Activity a WHERE a.user.id IN :userIds ORDER BY a.createdAt DESC")
    Page<Activity> findByUserIdIn(@Param("userIds") Collection<UUID> userIds, Pageable pageable);

    /**
     * EN: Deletes activities owned by the user or linked to that user's reviews (account cleanup).
     * KA: შლის მომხმარებლის აქტივობებს ან მის მიმოხილვებთან დაკავშირებულებს (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Activity a WHERE a.user.id = :userId OR a.review.id IN (SELECT r.id FROM Review r WHERE r.user.id = :userId)")
    void deleteByUserId(@Param("userId") UUID userId);

    /**
     * EN: Deletes feed activities linked to a review (required before deleting the review).
     * KA: შლის მიმოხილვასთან დაკავშირებულ ფიდის აქტივობებს (საჭიროა მიმოხილვის წაშლამდე).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Activity a WHERE a.review.id = :reviewId")
    void deleteAllByReviewId(@Param("reviewId") Long reviewId);
}
