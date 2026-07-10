package com.lecturboxd.auth;

import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.InvalidOtpException;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * EN: Creates, stores, and consumes email verification OTP codes for registration.
 * KA: ქმნის, ინახავს და მოიხმარს ელფოსტის ვერიფიკაციის OTP კოდებს რეგისტრაციისთვის.
 */
@Service
public class OtpService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final OtpGenerator otpGenerator;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMinutes;

    /**
     * EN: Wires repositories, OTP generator, password encoder, and OTP lifetime from config.
     * KA: აკავშირებს რეპოზიტორიებს, OTP გენერატორს, პაროლის ენკოდერს და OTP სიცოცხლის ხანგრძლივობას კონფიგიდან.
     */
    public OtpService(
            VerificationCodeRepository verificationCodeRepository,
            OtpGenerator otpGenerator,
            PasswordEncoder passwordEncoder,
            @Value("${lecturboxd.otp.expiration-minutes}") long expirationMinutes
    ) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.otpGenerator = otpGenerator;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    /**
     * EN: Invalidates prior codes for the email, then persists a new OTP with hashed password and expiry.
     * KA: აუქმებს წინა კოდებს ელფოსტისთვის, შემდეგ ინახავს ახალ OTP-ს ჰეშირებული პაროლით და ვადით.
     */
    @Transactional
    public VerificationCode createVerificationCode(String email, String name, String rawPassword) {
        invalidateExistingCodes(email);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email.trim().toLowerCase());
        verificationCode.setCode(otpGenerator.generateSixDigitCode());
        verificationCode.setExpiresAt(Instant.now().plusSeconds(expirationMinutes * 60));
        verificationCode.setUsed(false);
        verificationCode.setName(name.trim());
        // EN: Store only the password hash pending successful verification | KA: ინახება მხოლოდ პაროლის ჰეში წარმატებულ ვერიფიკაციამდე
        verificationCode.setPasswordHash(passwordEncoder.encode(rawPassword));

        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * EN: Validates the latest unused OTP for the email, marks it used, and returns the entity.
     * KA: ამოწმებს ელფოსტის უახლეს გამოუყენებელ OTP-ს, აღნიშნავს გამოყენებულად და აბრუნებს ენთითის.
     */
    @Transactional
    public VerificationCode validateAndConsume(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository
                .findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(email.trim())
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired verification code"));

        // EN: Defense-in-depth checks even though the query filters unused codes | KA: დამატებითი შემოწმებები, მიუხედავად იმისა, რომ მოთხოვნა უკვე ფილტრავს გამოუყენებელ კოდებს
        if (verificationCode.isUsed()) {
            throw new InvalidOtpException("Verification code has already been used");
        }

        if (verificationCode.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidOtpException("Verification code has expired");
        }

        if (!verificationCode.getCode().equals(code.trim())) {
            throw new InvalidOtpException("Invalid verification code");
        }

        verificationCode.setUsed(true);
        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * EN: Deletes all existing verification codes for the given email before issuing a new one.
     * KA: შლის ყველა არსებულ ვერიფიკაციის კოდს მოცემული ელფოსტისთვის ახლის გაცემამდე.
     */
    private void invalidateExistingCodes(String email) {
        verificationCodeRepository.deleteAllByEmail(email.trim());
    }
}
