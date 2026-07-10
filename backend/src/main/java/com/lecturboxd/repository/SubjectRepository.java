package com.lecturboxd.repository;

import com.lecturboxd.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * EN: Spring Data repository for Subject/course entities.
 * KA: Spring Data რეპოზიტორი Subject/კურსის ენთითებისთვის.
 */
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * EN: Subjects in a semester ordered by name ascending.
     * KA: სემესტრის საგნები სახელის ზრდადობით.
     */
    List<Subject> findBySemesterIdOrderByNameAsc(Long semesterId);

    /**
     * EN: Finds a subject by name (case-insensitive) within a semester.
     * KA: პოულობს საგანს სახელით (რეგისტრის გარეშე) სემესტრში.
     */
    Optional<Subject> findByNameIgnoreCaseAndSemesterId(String name, Long semesterId);

    /**
     * EN: Count of subjects in a semester.
     * KA: სემესტრის საგნების რაოდენობა.
     */
    long countBySemesterId(Long semesterId);
}
