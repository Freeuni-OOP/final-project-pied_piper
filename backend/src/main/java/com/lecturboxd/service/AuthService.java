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

/**
 * EN: Handles registration, OTP verification, login, and dev-only account reset.
 * KA: ამუშავებს რეგისტრაციას, OTP ვერიფიკაციას, შესვლას და დევ-მხოლოდ ანგარიშის რესეტს.
 */
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

    /**
     * EN: Starts registration by validating email, creating an OTP, and sending it.
     * KA: იწყებს რეგისტრაციას ელფოსტის ვალიდაციით, OTP-ის შექმნით და გაგზავნით.
     */
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // EN: Validate university email domain | KA: უნივერსიტეტის ელფოსტის დომენის ვალიდაცია
        universityEmailValidator.validate(email);

        // EN: Reject if account already exists | KA: უარყოფა თუ ანგარიში უკვე არსებობს
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        // EN: Create OTP verification code in DB | KA: OTP ვერიფიკაციის კოდის შექმნა ბაზაში
        VerificationCode verificationCode = otpService.createVerificationCode(
                email,
                request.getName(),
                request.getPassword()
        );

        // EN: Side effect — send verification email (or log in dev) | KA: გვერდითი ეფექტი — ვერიფიკაციის ელფოსტის გაგზავნა
        emailService.sendVerificationCode(email, verificationCode.getCode());

        RegisterResponse response = new RegisterResponse(
                "Verification code sent to your email",
                email,
                verificationCode.getExpiresAt()
        );
        // EN: Expose OTP in response when mail is log-only | KA: OTP-ის ჩვენება პასუხში, როცა მეილი მხოლოდ ლოგია
        if (mailLogOnly) {
            response.setDevCode(verificationCode.getCode());
            response.setMessage("Verification code generated (dev mode — check devCode below)");
        }
        return response;
    }

    /**
     * EN: Consumes a valid OTP and creates a verified user with a JWT.
     * KA: მოიხმარს ვალიდურ OTP-ს და ქმნის ვერიფიცირებულ მომხმარებელს JWT-ით.
     */
    @Transactional
    public AuthResponse verify(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // EN: Reject if account already exists | KA: უარყოფა თუ ანგარიში უკვე არსებობს
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        // EN: Validate and consume OTP | KA: OTP-ის ვალიდაცია და მოხმარება
        VerificationCode verificationCode = otpService.validateAndConsume(email, request.getCode());

        // EN: Persist verified user from OTP payload | KA: ვერიფიცირებული მომხმარებლის შენახვა OTP მონაცემებიდან
        User user = new User();
        user.setName(verificationCode.getName());
        user.setEmail(email);
        user.setPassword(verificationCode.getPasswordHash());
        user.setVerified(true);

        User savedUser = userRepository.save(user);
        // EN: Issue JWT for the new user | KA: JWT-ის გაცემა ახალი მომხმარებლისთვის
        String token = jwtTokenProvider.generateToken(savedUser);

        return new AuthResponse(token, userMapper.toResponse(savedUser));
    }

    /**
     * EN: Authenticates a verified user by email/password and returns a JWT.
     * KA: ავთენტიფიცირებს ვერიფიცირებულ მომხმარებელს ელფოსტა/პაროლით და აბრუნებს JWT-ს.
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // EN: Load user by email | KA: მომხმარებლის ჩატვირთვა ელფოსტით
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // EN: Require verified account | KA: საჭიროა ვერიფიცირებული ანგარიში
        if (!user.isVerified()) {
            throw new UnauthorizedException("Email address is not verified");
        }

        // EN: Validate password hash | KA: პაროლის ჰეშის შემოწმება
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // EN: Issue JWT | KA: JWT-ის გაცემა
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, userMapper.toResponse(user));
    }

    /**
     * EN: Dev-only helper that deletes a user and related verification codes by email.
     * KA: დევ-მხოლოდ დამხმარე, რომელიც შლის მომხმარებელს და დაკავშირებულ ვერიფიკაციის კოდებს ელფოსტით.
     */
    @Transactional
    public DevDeleteResponse deleteUserForDev(DeleteUserRequest request) {
        // EN: Guard — only when dev endpoints enabled | KA: დაცვა — მხოლოდ როცა დევ ენდპოინტები ჩართულია
        if (!devEnabled) {
            throw new ForbiddenException("Dev endpoints are disabled");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        boolean userDeleted = userRepository.findByEmailIgnoreCase(email).isPresent();
        int codesDeleted = verificationCodeRepository.findByEmailIgnoreCase(email).size();

        // EN: Cascade delete order — verification codes then user | KA: კასკადური წაშლის რიგი — ჯერ კოდები, შემდეგ მომხმარებელი
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
