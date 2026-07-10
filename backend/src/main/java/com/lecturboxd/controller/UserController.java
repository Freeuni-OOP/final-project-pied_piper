package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.UpdateProfileRequest;
import com.lecturboxd.dto.response.UserProfileResponse;
import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.service.UserService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal LecturboxdUserPrincipal principal) {
        return userService.getCurrentUser(principal.getId());
    }

    @PutMapping("/me")
    public UserResponse updateCurrentUser(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateCurrentUser(principal.getId(), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal LecturboxdUserPrincipal principal) {
        userService.deleteAccount(principal.getId());
    }

    @GetMapping("/search")
    public Page<UserResponse> searchUsers(
            @RequestParam String q,
            @ParameterObject Pageable pageable
    ) {
        return userService.searchUsers(q, pageable);
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable UUID userId) {
        return userService.getUserProfile(userId);
    }
}
