package com.lecturboxd.repository;

import com.lecturboxd.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

    Optional<VerificationCode> findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(String email);

    List<VerificationCode> findByEmailIgnoreCaseAndUsedFalse(String email);

    List<VerificationCode> findByEmailIgnoreCase(String email);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM VerificationCode v WHERE LOWER(v.email) = LOWER(:email)")
    int deleteAllByEmail(@Param("email") String email);
}
