package com.lecturboxd.repository;

import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * EN: Spring Data repository for Lecture syllabus entities.
 * KA: Spring Data რეპოზიტორი Lecture სილაბუსის ენთითებისთვის.
 */
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    /**
     * EN: Finds a lecture by title, week, type, and subject (import/dedup lookup).
     * KA: პოულობს ლექციას სათაურით, კვირით, ტიპით და საგნით (იმპორტის/დედუპლიკაციის ძებნა).
     */
    Optional<Lecture> findByTitleAndWeekAndTypeAndSubjectId(
            String title,
            Integer week,
            LectureType type,
            Long subjectId
    );

    /**
     * EN: All lectures for a subject ordered by week, lecture number, then type.
     * KA: საგნის ყველა ლექცია დალაგებული კვირით, ლექციის ნომრით, შემდეგ ტიპით.
     */
    List<Lecture> findBySubjectIdOrderByWeekAscLectureNumberAscTypeAsc(Long subjectId);

    /**
     * EN: Case-insensitive search of lectures by title or description substring.
     * KA: ლექციების რეგისტრისგან დამოუკიდებელი ძებნა სათაურის ან აღწერის ქვესტრიქონით.
     */
    @Query("""
            SELECT l FROM Lecture l
            WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(COALESCE(l.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY l.title ASC
            """)
    Page<Lecture> searchByTitleOrDescription(@Param("query") String query, Pageable pageable);
}
