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

/**
 * EN: Spring Data repository for User account entities.
 * KA: Spring Data რეპოზიტორი User ანგარიშის ენთითებისთვის.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * EN: Whether an account with this email already exists (case-insensitive).
     * KA: არსებობს თუ არა ანგარიში ამ ელფოსტით (რეგისტრის გარეშე).
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * EN: Loads a user by email ignoring case (login / principal lookup).
     * KA: იტვირთავს მომხმარებელს ელფოსტით რეგისტრის იგნორირებით (შესვლა / principal ძებნა).
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * EN: Case-insensitive paged search of users by name or email substring.
     * KA: მომხმარებლების რეგისტრისგან დამოუკიდებელი გვერდებად ძებნა სახელის ან ელფოსტის ქვესტრიქონით.
     */
    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY u.name ASC
            """)
    Page<User> searchByNameOrEmail(@Param("query") String query, Pageable pageable);

    /**
     * EN: Deletes all users matching the email (case-insensitive cleanup).
     * KA: შლის ყველა მომხმარებელს ელფოსტის მიხედვით (რეგისტრისგან დამოუკიდებელი გასუფთავება).
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    int deleteAllByEmail(@Param("email") String email);
}
