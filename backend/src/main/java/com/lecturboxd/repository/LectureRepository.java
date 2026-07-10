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

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findByTitleAndWeekAndTypeAndSubjectId(
            String title,
            Integer week,
            LectureType type,
            Long subjectId
    );

    List<Lecture> findBySubjectIdOrderByWeekAscLectureNumberAscTypeAsc(Long subjectId);

    @Query("""
            SELECT l FROM Lecture l
            WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(COALESCE(l.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY l.title ASC
            """)
    Page<Lecture> searchByTitleOrDescription(@Param("query") String query, Pageable pageable);
}
