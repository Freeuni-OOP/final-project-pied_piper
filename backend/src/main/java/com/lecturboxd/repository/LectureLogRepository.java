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

/**
 * EN: Spring Data repository for LectureLog diary entries.
 * KA: Spring Data რეპოზიტორი LectureLog დღიურის ჩანაწერებისთვის.
 */
public interface LectureLogRepository extends JpaRepository<LectureLog, Long> {

    /**
     * EN: Paged lecture logs for a user ordered by watched date then created time (newest first).
     * KA: მომხმარებლის ლექციის ლოგები გვერდებად, ნახვის თარიღისა და შექმნის დროის მიხედვით (უახლესი პირველი).
     */
    Page<LectureLog> findByUserIdOrderByWatchedAtDescCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * EN: Finds the single log for a user+lecture pair if it exists.
     * KA: პოულობს ერთადერთ ლოგს user+lecture წყვილისთვის, თუ არსებობს.
     */
    Optional<LectureLog> findByUserIdAndLectureId(UUID userId, Long lectureId);

    /**
     * EN: Total number of lectures the user has logged.
     * KA: მომხმარებლის მიერ დალოგებული ლექციების საერთო რაოდენობა.
     */
    long countByUserId(UUID userId);

    /**
     * EN: Whether the user already logged the given lecture.
     * KA: უკვე დაალოგა თუ არა მომხმარებელმა მოცემული ლექცია.
     */
    boolean existsByUserIdAndLectureId(UUID userId, Long lectureId);

    /**
     * EN: Deletes all lecture logs for a user (account cleanup).
     * KA: შლის მომხმარებლის ყველა ლექციის ლოგს (ანგარიშის გასუფთავება).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM LectureLog l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
