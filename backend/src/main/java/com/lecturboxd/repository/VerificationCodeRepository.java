package com.lecturboxd.repository;

import com.lecturboxd.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * EN: Spring Data repository for pending signup VerificationCode rows.
 * KA: Spring Data რეპოზიტორი მოლოდინში მყოფი რეგისტრაციის VerificationCode ჩანაწერებისთვის.
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

    /**
     * EN: Latest unused OTP for an email (by furthest expiry), for verify/resend flows.
     * KA: ელფოსტის უახლესი გამოუყენებელი OTP (ყველაზე შორს ვადით), ვერიფიკაციის/ხელახალი გაგზავნისთვის.
     */
    Optional<VerificationCode> findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(String email);

    /**
     * EN: All unused verification codes for an email.
     * KA: ელფოსტის ყველა გამოუყენებელი ვერიფიკაციის კოდი.
     */
    List<VerificationCode> findByEmailIgnoreCaseAndUsedFalse(String email);

    /**
     * EN: All verification codes for an email (used and unused).
     * KA: ელფოსტის ყველა ვერიფიკაციის კოდი (გამოყენებული და გამოუყენებელი).
     */
    List<VerificationCode> findByEmailIgnoreCase(String email);

    /**
     * EN: Deletes all verification codes for an email (case-insensitive cleanup).
     * KA: შლის ელფოსტის ყველა ვერიფიკაციის კოდს (რეგისტრისგან დამოუკიდებელი გასუფთავება).
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM VerificationCode v WHERE LOWER(v.email) = LOWER(:email)")
    int deleteAllByEmail(@Param("email") String email);
}
