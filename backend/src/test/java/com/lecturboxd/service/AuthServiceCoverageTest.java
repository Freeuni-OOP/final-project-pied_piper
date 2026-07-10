package com.lecturboxd.service;

import com.lecturboxd.auth.JwtTokenProvider;
import com.lecturboxd.auth.OtpService;
import com.lecturboxd.auth.UniversityEmailValidator;
import com.lecturboxd.dto.mapper.UserMapper;
import com.lecturboxd.dto.request.DeleteUserRequest;
import com.lecturboxd.dto.request.LoginRequest;
import com.lecturboxd.dto.request.RegisterRequest;
import com.lecturboxd.dto.request.VerifyOtpRequest;
import com.lecturboxd.dto.response.AuthResponse;
import com.lecturboxd.dto.response.DevDeleteResponse;
import com.lecturboxd.dto.response.RegisterResponse;
import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.UnauthorizedException;
import com.lecturboxd.exception.UserAlreadyExistsException;
import com.lecturboxd.repository.UserRepository;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceCoverageTest {

    @Mock private UserRepository userRepository;
    @Mock private VerificationCodeRepository verificationCodeRepository;
    @Mock private UniversityEmailValidator universityEmailValidator;
    @Mock private OtpService otpService;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserMapper userMapper;

    @Test
    void registerIncludesDevCodeWhenMailLogOnly() {
        AuthService authService = authService(true, false);
        RegisterRequest request = new RegisterRequest();
        request.setName("Test");
        request.setEmail("student@freeuni.edu.ge");
        request.setPassword("password123");

        VerificationCode code = new VerificationCode();
        code.setCode("123456");
        code.setExpiresAt(Instant.now().plusSeconds(600));

        doNothing().when(universityEmailValidator).validate(any());
        when(userRepository.existsByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(false);
        when(otpService.createVerificationCode(any(), any(), any())).thenReturn(code);
        doNothing().when(emailService).sendVerificationCode(any(), any());

        RegisterResponse response = authService.register(request);
        assertEquals("123456", response.getDevCode());
        assertTrue(response.getMessage().contains("dev mode"));
    }

    @Test
    void verifyRejectsExistingEmail() {
        AuthService authService = authService(false, false);
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("student@freeuni.edu.ge");
        request.setCode("123456");
        when(userRepository.existsByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.verify(request));
    }

    @Test
    void loginPaths() {
        AuthService authService = authService(false, false);
        LoginRequest request = new LoginRequest();
        request.setEmail("student@freeuni.edu.ge");
        request.setPassword("password123");

        when(userRepository.findByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> authService.login(request));

        User unverified = user(false);
        when(userRepository.findByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(Optional.of(unverified));
        assertThrows(UnauthorizedException.class, () -> authService.login(request));

        User verified = user(true);
        when(userRepository.findByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(Optional.of(verified));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(jwtTokenProvider.generateToken(verified)).thenReturn("token");
        when(userMapper.toResponse(verified)).thenReturn(new UserResponse(verified.getId(), "T", verified.getEmail(), true));

        AuthResponse response = authService.login(request);
        assertEquals("token", response.getToken());
    }

    @Test
    void deleteUserForDevPaths() {
        AuthService disabled = authService(false, false);
        DeleteUserRequest request = new DeleteUserRequest();
        request.setEmail("student@freeuni.edu.ge");
        assertThrows(ForbiddenException.class, () -> disabled.deleteUserForDev(request));

        AuthService enabled = authService(false, true);
        User user = user(true);
        when(userRepository.findByEmailIgnoreCase("student@freeuni.edu.ge")).thenReturn(Optional.of(user));
        when(verificationCodeRepository.findByEmailIgnoreCase("student@freeuni.edu.ge"))
                .thenReturn(List.of(new VerificationCode(), new VerificationCode()));

        DevDeleteResponse response = enabled.deleteUserForDev(request);
        assertTrue(response.isUserDeleted());
        assertEquals(2, response.getVerificationCodesDeleted());
        verify(verificationCodeRepository).deleteAllByEmail("student@freeuni.edu.ge");
        verify(userRepository).deleteAllByEmail("student@freeuni.edu.ge");
    }

    private AuthService authService(boolean mailLogOnly, boolean devEnabled) {
        return new AuthService(
                userRepository,
                verificationCodeRepository,
                universityEmailValidator,
                otpService,
                emailService,
                passwordEncoder,
                jwtTokenProvider,
                userMapper,
                mailLogOnly,
                devEnabled
        );
    }

    private static User user(boolean verified) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("T");
        user.setEmail("student@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(verified);
        return user;
    }
}
