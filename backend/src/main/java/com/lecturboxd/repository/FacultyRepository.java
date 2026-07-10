package com.lecturboxd.repository;

import com.lecturboxd.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * EN: Spring Data repository for Faculty catalog entities.
 * KA: Spring Data რეპოზიტორი Faculty კატალოგის ენთითებისთვის.
 */
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    /**
     * EN: Finds a faculty by name ignoring case.
     * KA: პოულობს ფაკულტეტს სახელით რეგისტრის იგნორირებით.
     */
    Optional<Faculty> findByNameIgnoreCase(String name);

    /**
     * EN: Checks whether a faculty with the given name already exists (case-insensitive).
     * KA: ამოწმებს, არსებობს თუ არა ფაკულტეტი მოცემული სახელით (რეგისტრის გარეშე).
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * EN: Checks name uniqueness excluding a specific faculty id (for updates).
     * KA: ამოწმებს სახელის უნიკალურობას კონკრეტული faculty id-ის გამორიცხვით (განახლებისთვის).
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
