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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/verify")
    public AuthResponse verify(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verify(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/dev/delete-user")
    public DevDeleteResponse deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        return authService.deleteUserForDev(request);
    }
}
