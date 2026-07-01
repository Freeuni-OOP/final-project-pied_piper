package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/lectures/{lectureId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long lectureId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.createReview(principal.getId(), lectureId, request);
    }

    @PutMapping("/api/reviews/{id}")
    public ReviewResponse updateReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable("id") Long reviewId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.updateReview(principal.getId(), reviewId, request);
    }

    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable("id") Long reviewId
    ) {
        reviewService.deleteReview(principal.getId(), reviewId);
    }

    @GetMapping("/api/lectures/{lectureId}/reviews")
    public Page<ReviewResponse> getReviewsForLecture(@PathVariable Long lectureId, Pageable pageable) {
        return reviewService.getReviewsForLecture(lectureId, pageable);
    }

    @GetMapping("/api/lectures/{lectureId}/rating-summary")
    public RatingSummaryResponse getRatingSummary(@PathVariable Long lectureId) {
        return reviewService.getRatingSummary(lectureId);
    }

    @GetMapping("/api/users/{userId}/reviews")
    public Page<ReviewResponse> getReviewsByUser(@PathVariable UUID userId, Pageable pageable) {
        return reviewService.getReviewsByUser(userId, pageable);
    }
}
