package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.service.ReviewService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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

/**
 * EN: Review API — create/update/delete reviews, list by lecture or user, and rating summaries.
 * KA: მიმოხილვების API — მიმოხილვების შექმნა/განახლება/წაშლა, სია ლექციით ან მომხმარებლით და რეიტინგის შეჯამება.
 */
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * EN: POST /api/lectures/{lectureId}/reviews — creates a review for a lecture (JWT required).
     * KA: POST /api/lectures/{lectureId}/reviews — ქმნის მიმოხილვას ლექციისთვის (საჭიროა JWT).
     */
    @PostMapping("/api/lectures/{lectureId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long lectureId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.createReview(principal.getId(), lectureId, request);
    }

    /**
     * EN: PUT /api/reviews/{id} — updates an existing review owned by the current user (JWT required).
     * KA: PUT /api/reviews/{id} — განაახლებს არსებულ მიმოხილვას, რომელიც მიმდინარე მომხმარებლისაა (საჭიროა JWT).
     */
    @PutMapping("/api/reviews/{id}")
    public ReviewResponse updateReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable("id") Long reviewId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.updateReview(principal.getId(), reviewId, request);
    }

    /**
     * EN: DELETE /api/reviews/{id} — deletes a review owned by the current user (JWT required).
     * KA: DELETE /api/reviews/{id} — შლის მიმოხილვას, რომელიც მიმდინარე მომხმარებლისაა (საჭიროა JWT).
     */
    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable("id") Long reviewId
    ) {
        reviewService.deleteReview(principal.getId(), reviewId);
    }

    /**
     * EN: GET /api/lectures/{lectureId}/reviews — paginated reviews for a lecture.
     * KA: GET /api/lectures/{lectureId}/reviews — ლექციის მიმოხილვები გვერდებად.
     */
    @GetMapping("/api/lectures/{lectureId}/reviews")
    public Page<ReviewResponse> getReviewsForLecture(
            @PathVariable Long lectureId, 
            @ParameterObject Pageable pageable
    ) {
        return reviewService.getReviewsForLecture(lectureId, pageable);
    }

    /**
     * EN: GET /api/lectures/{lectureId}/rating-summary — aggregate rating stats for a lecture.
     * KA: GET /api/lectures/{lectureId}/rating-summary — ლექციის რეიტინგის აგრეგირებული სტატისტიკა.
     */
    @GetMapping("/api/lectures/{lectureId}/rating-summary")
    public RatingSummaryResponse getRatingSummary(@PathVariable Long lectureId) {
        return reviewService.getRatingSummary(lectureId);
    }

    /**
     * EN: GET /api/users/{userId}/reviews — paginated reviews written by a user.
     * KA: GET /api/users/{userId}/reviews — მომხმარებლის დაწერილი მიმოხილვები გვერდებად.
     */
    @GetMapping("/api/users/{userId}/reviews")
    public Page<ReviewResponse> getReviewsByUser(
            @PathVariable UUID userId, 
            @ParameterObject Pageable pageable
    ) {
        return reviewService.getReviewsByUser(userId, pageable);
    }
}
