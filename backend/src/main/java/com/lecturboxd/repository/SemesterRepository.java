package com.lecturboxd.repository;

import com.lecturboxd.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * EN: Spring Data repository for Semester entities.
 * KA: Spring Data რეპოზიტორი Semester ენთითებისთვის.
 */
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    /**
     * EN: Semesters for a faculty ordered by semester number ascending.
     * KA: ფაკულტეტის სემესტრები ნომრის ზრდადობით.
     */
    List<Semester> findByFacultyIdOrderByNumberAsc(Long facultyId);

    /**
     * EN: Finds a semester by number within a faculty.
     * KA: პოულობს სემესტრს ნომრით ფაკულტეტში.
     */
    Optional<Semester> findByNumberAndFacultyId(String number, Long facultyId);

    /**
     * EN: Whether a semester number already exists under the faculty.
     * KA: არსებობს თუ არა უკვე სემესტრის ნომერი ამ ფაკულტეტში.
     */
    boolean existsByNumberAndFacultyId(String number, Long facultyId);

    /**
     * EN: Checks number uniqueness under a faculty excluding a specific semester id (for updates).
     * KA: ამოწმებს ნომრის უნიკალურობას ფაკულტეტში კონკრეტული semester id-ის გამორიცხვით (განახლებისთვის).
     */
    boolean existsByNumberAndFacultyIdAndIdNot(String number, Long facultyId, Long id);

    /**
     * EN: Count of semesters belonging to a faculty.
     * KA: ფაკულტეტის სემესტრების რაოდენობა.
     */
    long countByFacultyId(Long facultyId);
}
