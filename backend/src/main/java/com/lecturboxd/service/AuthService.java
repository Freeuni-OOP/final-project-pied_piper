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
import com.lecturboxd.entity.User;
import com.lecturboxd.entity.VerificationCode;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.UnauthorizedException;
import com.lecturboxd.exception.UserAlreadyExistsException;
import com.lecturboxd.repository.UserRepository;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UniversityEmailValidator universityEmailValidator;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final boolean mailLogOnly;
    private final boolean devEnabled;

    public AuthService(
            UserRepository userRepository,
            VerificationCodeRepository verificationCodeRepository,
            UniversityEmailValidator universityEmailValidator,
            OtpService otpService,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            UserMapper userMapper,
            @Value("${lecturboxd.mail.log-only:false}") boolean mailLogOnly,
            @Value("${lecturboxd.dev.enabled:false}") boolean devEnabled
    ) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.universityEmailValidator = universityEmailValidator;
        this.otpService = otpService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
        this.mailLogOnly = mailLogOnly;
        this.devEnabled = devEnabled;
    }

    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        universityEmailValidator.validate(email);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        VerificationCode verificationCode = otpService.createVerificationCode(
                email,
                request.getName(),
                request.getPassword()
        );

        emailService.sendVerificationCode(email, verificationCode.getCode());

        RegisterResponse response = new RegisterResponse(
                "Verification code sent to your email",
                email,
                verificationCode.getExpiresAt()
        );
        if (mailLogOnly) {
            response.setDevCode(verificationCode.getCode());
            response.setMessage("Verification code generated (dev mode — check devCode below)");
        }
        return response;
    }

    @Transactional
    public AuthResponse verify(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        VerificationCode verificationCode = otpService.validateAndConsume(email, request.getCode());

        User user = new User();
        user.setName(verificationCode.getName());
        user.setEmail(email);
        user.setPassword(verificationCode.getPasswordHash());
        user.setVerified(true);

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser);

        return new AuthResponse(token, userMapper.toResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isVerified()) {
            throw new UnauthorizedException("Email address is not verified");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, userMapper.toResponse(user));
    }

    @Transactional
    public DevDeleteResponse deleteUserForDev(DeleteUserRequest request) {
        if (!devEnabled) {
            throw new ForbiddenException("Dev endpoints are disabled");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        boolean userDeleted = userRepository.findByEmailIgnoreCase(email).isPresent();
        int codesDeleted = verificationCodeRepository.findByEmailIgnoreCase(email).size();

        verificationCodeRepository.deleteAllByEmail(email);
        userRepository.deleteAllByEmail(email);

        return new DevDeleteResponse(
                "Reset complete — you can register again with this email",
                email,
                userDeleted,
                codesDeleted
        );
    }
}
