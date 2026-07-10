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

/**
 * EN: User profile API — current user CRUD, search, and public profile lookup.
 * KA: მომხმარებლის პროფილის API — მიმდინარე მომხმარებლის CRUD, ძებნა და საჯარო პროფილის ნახვა.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * EN: GET /api/users/me — returns the authenticated user's profile (JWT required).
     * KA: GET /api/users/me — აბრუნებს ავთენტიფიცირებული მომხმარებლის პროფილს (საჭიროა JWT).
     */
    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal LecturboxdUserPrincipal principal) {
        return userService.getCurrentUser(principal.getId());
    }

    /**
     * EN: PUT /api/users/me — updates the authenticated user's profile (JWT required).
     * KA: PUT /api/users/me — განაახლებს ავთენტიფიცირებული მომხმარებლის პროფილს (საჭიროა JWT).
     */
    @PutMapping("/me")
    public UserResponse updateCurrentUser(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateCurrentUser(principal.getId(), request);
    }

    /**
     * EN: DELETE /api/users/me — permanently deletes the authenticated user's account (JWT required).
     * KA: DELETE /api/users/me — სამუდამოდ შლის ავთენტიფიცირებული მომხმარებლის ანგარიშს (საჭიროა JWT).
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal LecturboxdUserPrincipal principal) {
        userService.deleteAccount(principal.getId());
    }

    /**
     * EN: GET /api/users/search?q= — paginated user search by query string.
     * KA: GET /api/users/search?q= — მომხმარებლების გვერდებად დაყოფილი ძებნა მოთხოვნის სტრიქონით.
     */
    @GetMapping("/search")
    public Page<UserResponse> searchUsers(
            @RequestParam String q,
            @ParameterObject Pageable pageable
    ) {
        return userService.searchUsers(q, pageable);
    }

    /**
     * EN: GET /api/users/{userId} — returns a public user profile by ID.
     * KA: GET /api/users/{userId} — აბრუნებს მომხმარებლის საჯარო პროფილს ID-ით.
     */
    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable UUID userId) {
        return userService.getUserProfile(userId);
    }
}
