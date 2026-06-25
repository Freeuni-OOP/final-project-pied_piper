package com.lecturboxd.service;

import com.lecturboxd.auth.JwtTokenProvider;
import com.lecturboxd.auth.OtpService;
import com.lecturboxd.auth.UniversityEmailValidator;
import com.lecturboxd.dto.mapper.UserMapper;
import com.lecturboxd.dto.request.LoginRequest;
import com.lecturboxd.dto.request.RegisterRequest;
import com.lecturboxd.dto.request.VerifyOtpRequest;
import com.lecturboxd.dto.response.AuthResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.UnauthorizedException;
import com.lecturboxd.exception.UserAlreadyExistsException;
import com.lecturboxd.repository.UserRepository;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private UniversityEmailValidator universityEmailValidator;
    @Mock
    private OtpService otpService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserMapper userMapper;

    private AuthService authService;

    private RegisterRequest registerRequest;
    private VerificationCode verificationCode;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                verificationCodeRepository,
                universityEmailValidator,
                otpService,
                emailService,
                passwordEncoder,
                jwtTokenProvider,
                userMapper,
                false,
                false
        );

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("student@freeuni.edu.ge");
        registerRequest.setPassword("password123");

        verificationCode = new VerificationCode();
        verificationCode.setEmail("student@freeuni.edu.ge");
        verificationCode.setCode("123456");
        verificationCode.setExpiresAt(Instant.now().plusSeconds(600));
        verificationCode.setName("Test User");
        verificationCode.setPasswordHash("hashed-password");
    }

    @Test
    void registerSendsOtp() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(otpService.createVerificationCode(anyString(), anyString(), anyString())).thenReturn(verificationCode);
        doNothing().when(universityEmailValidator).validate(anyString());
        doNothing().when(emailService).sendVerificationCode(anyString(), anyString());

        var response = authService.register(registerRequest);

        assertEquals("student@freeuni.edu.ge", response.getEmail());
        verify(emailService).sendVerificationCode("student@freeuni.edu.ge", "123456");
    }

    @Test
    void registerRejectsExistingUser() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);
        doNothing().when(universityEmailValidator).validate(anyString());

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User();
        user.setEmail("student@freeuni.edu.ge");
        user.setPassword("hashed-password");
        user.setVerified(true);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("student@freeuni.edu.ge");
        loginRequest.setPassword("wrong-password");

        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void verifyCreatesUserAndReturnsToken() {
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setEmail("student@freeuni.edu.ge");
        verifyRequest.setCode("123456");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("student@freeuni.edu.ge");
        savedUser.setName("Test User");
        savedUser.setVerified(true);

        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(otpService.validateAndConsume(anyString(), anyString())).thenReturn(verificationCode);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken(savedUser)).thenReturn("jwt-token");
        when(userMapper.toResponse(savedUser)).thenReturn(
                new com.lecturboxd.dto.response.UserResponse(
                        savedUser.getId(), savedUser.getName(), savedUser.getEmail(), true
                )
        );

        AuthResponse response = authService.verify(verifyRequest);

        assertEquals("jwt-token", response.getToken());
    }
}
