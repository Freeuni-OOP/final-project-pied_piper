package com.lecturboxd.repository;

import com.lecturboxd.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findByFacultyIdOrderByNumberAsc(Long facultyId);

    Optional<Semester> findByNumberAndFacultyId(String number, Long facultyId);

    boolean existsByNumberAndFacultyId(String number, Long facultyId);

    boolean existsByNumberAndFacultyIdAndIdNot(String number, Long facultyId, Long id);

    long countByFacultyId(Long facultyId);
}
