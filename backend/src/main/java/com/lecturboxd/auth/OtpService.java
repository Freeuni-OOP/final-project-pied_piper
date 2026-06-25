package com.lecturboxd.auth;

import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.InvalidOtpException;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OtpService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final OtpGenerator otpGenerator;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMinutes;

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

    @Transactional
    public VerificationCode createVerificationCode(String email, String name, String rawPassword) {
        invalidateExistingCodes(email);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email.trim().toLowerCase());
        verificationCode.setCode(otpGenerator.generateSixDigitCode());
        verificationCode.setExpiresAt(Instant.now().plusSeconds(expirationMinutes * 60));
        verificationCode.setUsed(false);
        verificationCode.setName(name.trim());
        verificationCode.setPasswordHash(passwordEncoder.encode(rawPassword));

        return verificationCodeRepository.save(verificationCode);
    }

    @Transactional
    public VerificationCode validateAndConsume(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository
                .findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(email.trim())
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired verification code"));

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

    private void invalidateExistingCodes(String email) {
        verificationCodeRepository.deleteAllByEmail(email.trim());
    }
}
