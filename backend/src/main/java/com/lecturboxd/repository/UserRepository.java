package com.lecturboxd.repository;

import com.lecturboxd.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY u.name ASC
            """)
    Page<User> searchByNameOrEmail(@Param("query") String query, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    int deleteAllByEmail(@Param("email") String email);
}
