package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.response.FollowStatusResponse;
import com.lecturboxd.dto.response.FollowUserResponse;
import com.lecturboxd.service.FollowService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * EN: Follow API — follow/unfollow users, check status, and list followers/following.
 * KA: გამოწერის API — მომხმარებლების გამოწერა/გაუქმება, სტატუსის შემოწმება და გამომწერების/გამოწერილების სია.
 */
@RestController
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * EN: POST /api/users/{userId}/follow — follows the target user (JWT required).
     * KA: POST /api/users/{userId}/follow — აწერს სამიზნე მომხმარებელს (საჭიროა JWT).
     */
    @PostMapping("/api/users/{userId}/follow")
    public FollowStatusResponse follow(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.follow(principal.getId(), userId);
    }

    /**
     * EN: DELETE /api/users/{userId}/follow — unfollows the target user (JWT required).
     * KA: DELETE /api/users/{userId}/follow — აუქმებს სამიზნე მომხმარებლის გამოწერას (საჭიროა JWT).
     */
    @DeleteMapping("/api/users/{userId}/follow")
    public FollowStatusResponse unfollow(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.unfollow(principal.getId(), userId);
    }

    /**
     * EN: GET /api/users/{userId}/follow-status — whether the current user follows the target (JWT required).
     * KA: GET /api/users/{userId}/follow-status — აწერს თუ არა მიმდინარე მომხმარებელი სამიზნეს (საჭიროა JWT).
     */
    @GetMapping("/api/users/{userId}/follow-status")
    public FollowStatusResponse followStatus(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.getFollowStatus(principal.getId(), userId);
    }

    /**
     * EN: GET /api/users/{userId}/followers — lists users who follow the given user.
     * KA: GET /api/users/{userId}/followers — აბრუნებს მომხმარებლებს, რომლებიც აწერენ მოცემულ მომხმარებელს.
     */
    @GetMapping("/api/users/{userId}/followers")
    public List<FollowUserResponse> followers(@PathVariable UUID userId) {
        return followService.getFollowers(userId);
    }

    /**
     * EN: GET /api/users/{userId}/following — lists users the given user follows.
     * KA: GET /api/users/{userId}/following — აბრუნებს მომხმარებლებს, რომლებსაც მოცემული მომხმარებელი აწერს.
     */
    @GetMapping("/api/users/{userId}/following")
    public List<FollowUserResponse> following(@PathVariable UUID userId) {
        return followService.getFollowing(userId);
    }
}
