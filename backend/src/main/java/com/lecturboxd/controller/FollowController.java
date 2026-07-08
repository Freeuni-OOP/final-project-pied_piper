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

@RestController
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/api/users/{userId}/follow")
    public FollowStatusResponse follow(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.follow(principal.getId(), userId);
    }

    @DeleteMapping("/api/users/{userId}/follow")
    public FollowStatusResponse unfollow(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.unfollow(principal.getId(), userId);
    }

    @GetMapping("/api/users/{userId}/follow-status")
    public FollowStatusResponse followStatus(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        return followService.getFollowStatus(principal.getId(), userId);
    }

    @GetMapping("/api/users/{userId}/followers")
    public List<FollowUserResponse> followers(@PathVariable UUID userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/api/users/{userId}/following")
    public List<FollowUserResponse> following(@PathVariable UUID userId) {
        return followService.getFollowing(userId);
    }
}
