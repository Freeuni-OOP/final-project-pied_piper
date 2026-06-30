package com.lecturboxd.repository;

import com.lecturboxd.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findBySemesterIdOrderByNameAsc(Long semesterId);

    Optional<Subject> findByNameIgnoreCaseAndSemesterId(String name, Long semesterId);

    long countBySemesterId(Long semesterId);
}
