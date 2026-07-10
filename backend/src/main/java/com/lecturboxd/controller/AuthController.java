package com.lecturboxd.controller;

import com.lecturboxd.dto.request.DeleteUserRequest;
import com.lecturboxd.dto.request.LoginRequest;
import com.lecturboxd.dto.request.RegisterRequest;
import com.lecturboxd.dto.request.VerifyOtpRequest;
import com.lecturboxd.dto.response.AuthResponse;
import com.lecturboxd.dto.response.DevDeleteResponse;
import com.lecturboxd.dto.response.RegisterResponse;
import com.lecturboxd.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * EN: Authentication API — registration, OTP verification, login, and a dev-only user delete helper.
 * KA: ავთენტიფიკაციის API — რეგისტრაცია, OTP ვერიფიკაცია, შესვლა და დეველოპმენტისთვის მომხმარებლის წაშლის დამხმარე.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * EN: POST /api/auth/register — starts registration and sends an OTP (public, no JWT).
     * KA: POST /api/auth/register — იწყებს რეგისტრაციას და აგზავნის OTP-ს (საჯარო, JWT არ სჭირდება).
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * EN: POST /api/auth/verify — verifies OTP and returns a JWT auth response (public).
     * KA: POST /api/auth/verify — ამოწმებს OTP-ს და აბრუნებს JWT ავთენტიფიკაციის პასუხს (საჯარო).
     */
    @PostMapping("/verify")
    public AuthResponse verify(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verify(request);
    }

    /**
     * EN: POST /api/auth/login — authenticates with email/password and returns a JWT (public).
     * KA: POST /api/auth/login — ავთენტიფიცირებს ელფოსტით/პაროლით და აბრუნებს JWT-ს (საჯარო).
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * EN: POST /api/auth/dev/delete-user — development helper to delete a user by email (not for production).
     * KA: POST /api/auth/dev/delete-user — დეველოპმენტის დამხმარე მომხმარებლის წასაშლელად ელფოსტით (არა პროდაქშენისთვის).
     */
    @PostMapping("/dev/delete-user")
    public DevDeleteResponse deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        return authService.deleteUserForDev(request);
    }
}
