package com.lecturboxd.repository;

import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
