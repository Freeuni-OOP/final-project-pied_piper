package com.lecturboxd.auth;

import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.InvalidOtpException;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private OtpGenerator otpGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(verificationCodeRepository, otpGenerator, passwordEncoder, 10);
    }

    @Test
    void createVerificationCodeHashesPasswordAndStoresOtp() {
        when(otpGenerator.generateSixDigitCode()).thenReturn("654321");
        when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenAnswer(inv -> inv.getArgument(0));

        VerificationCode saved = otpService.createVerificationCode(" Student@FreeUni.edu.ge ", "Mate", "secret123");

        assertEquals("student@freeuni.edu.ge", saved.getEmail());
        assertEquals("654321", saved.getCode());
        assertEquals("bcrypt-hash", saved.getPasswordHash());
        assertEquals("Mate", saved.getName());
        verify(verificationCodeRepository).deleteAllByEmail("Student@FreeUni.edu.ge".trim());
    }

    @Test
    void validateAndConsumeMarksCodeUsed() {
        VerificationCode code = new VerificationCode();
        code.setEmail("student@freeuni.edu.ge");
        code.setCode("111222");
        code.setUsed(false);
        code.setExpiresAt(Instant.now().plusSeconds(600));

        when(verificationCodeRepository.findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc("student@freeuni.edu.ge"))
                .thenReturn(Optional.of(code));
        when(verificationCodeRepository.save(code)).thenReturn(code);

        VerificationCode result = otpService.validateAndConsume("student@freeuni.edu.ge", "111222");

        assertTrue(result.isUsed());
        verify(verificationCodeRepository).save(code);
    }

    @Test
    void validateAndConsumeRejectsWrongCode() {
        VerificationCode code = new VerificationCode();
        code.setCode("111222");
        code.setUsed(false);
        code.setExpiresAt(Instant.now().plusSeconds(600));

        when(verificationCodeRepository.findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(anyString()))
                .thenReturn(Optional.of(code));

        assertThrows(InvalidOtpException.class,
                () -> otpService.validateAndConsume("student@freeuni.edu.ge", "000000"));
    }

    @Test
    void validateAndConsumeRejectsExpiredCode() {
        VerificationCode code = new VerificationCode();
        code.setCode("111222");
        code.setUsed(false);
        code.setExpiresAt(Instant.now().minusSeconds(1));

        when(verificationCodeRepository.findTopByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(anyString()))
                .thenReturn(Optional.of(code));

        assertThrows(InvalidOtpException.class,
                () -> otpService.validateAndConsume("student@freeuni.edu.ge", "111222"));
    }
}
