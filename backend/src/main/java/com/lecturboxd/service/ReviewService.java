package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.ReviewMapper;
import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public ReviewService(
            ReviewRepository reviewRepository,
            LectureRepository lectureRepository,
            UserRepository userRepository,
            ActivityService activityService
    ) {
        this.reviewRepository = reviewRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    @Transactional
    public ReviewResponse createReview(UUID userId, Long lectureId, ReviewRequest request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + lectureId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (reviewRepository.findByUserIdAndLectureId(userId, lectureId).isPresent()) {
            throw new ConflictException("A review for this lecture already exists. Use the update endpoint to modify it.");
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(user);
        review.setLecture(lecture);

        Review saved = reviewRepository.save(review);
        activityService.recordReviewCreated(saved);
        return ReviewMapper.toResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(UUID userId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return ReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForLecture(Long lectureId, Pageable pageable) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new ResourceNotFoundException("Lecture not found with id " + lectureId);
        }

        return reviewRepository.findByLectureId(lectureId, pageable)
                .map(ReviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return reviewRepository.findByUserId(userId, pageable)
                .map(ReviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RatingSummaryResponse getRatingSummary(Long lectureId) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new ResourceNotFoundException("Lecture not found with id " + lectureId);
        }

        Object[] result = reviewRepository.getRatingSummaryByLectureId(lectureId);
        Double averageRating = 0.0;
        Long totalReviews = 0L;

        if (result != null && result.length >= 2) {
            if (result[0] != null) {
                averageRating = ((Number) result[0]).doubleValue();
            }
            if (result[1] != null) {
                totalReviews = ((Number) result[1]).longValue();
            }
        }

        if (totalReviews == 0L) {
            averageRating = 0.0;
        }

        return new RatingSummaryResponse(lectureId, averageRating, totalReviews);
    }
}
