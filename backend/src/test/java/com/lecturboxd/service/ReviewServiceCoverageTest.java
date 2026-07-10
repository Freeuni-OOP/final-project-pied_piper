package com.lecturboxd.service;

import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.ActivityRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceCoverageTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private LectureRepository lectureRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActivityService activityService;
    @Mock private ActivityRepository activityRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReviewThrowsWhenLectureOrUserMissing() {
        UUID userId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setRating(5);
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(userId, 1L, request));

        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture(1L)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(userId, 1L, request));
    }

    @Test
    void updateReviewSuccessAndMissing() {
        UUID owner = UUID.randomUUID();
        Review review = review(10L, owner, lecture(1L));
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewRequest request = new ReviewRequest();
        request.setRating(3);
        request.setComment("ok");

        ReviewResponse response = reviewService.updateReview(owner, 10L, request);
        assertEquals(3, response.getRating());
        assertEquals("ok", response.getComment());

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(owner, 99L, request));
    }

    @Test
    void deleteReviewPaths() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        Review review = review(10L, owner, lecture(1L));

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        assertThrows(ForbiddenException.class, () -> reviewService.deleteReview(other, 10L));
        verify(reviewRepository, never()).delete(any());

        reviewService.deleteReview(owner, 10L);
        verify(activityRepository).deleteAllByReviewId(10L);
        verify(reviewRepository).delete(review);

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(owner, 99L));
    }

    @Test
    void getReviewsForLectureAndUser() {
        UUID userId = UUID.randomUUID();
        Review review = review(1L, userId, lecture(2L));
        Page<Review> page = new PageImpl<>(List.of(review));

        when(lectureRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewsForLecture(2L, Pageable.unpaged()));

        when(lectureRepository.existsById(2L)).thenReturn(true);
        when(reviewRepository.findByLectureId(2L, Pageable.unpaged())).thenReturn(page);
        assertEquals(1, reviewService.getReviewsForLecture(2L, Pageable.unpaged()).getTotalElements());

        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewsByUser(userId, Pageable.unpaged()));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(reviewRepository.findByUserId(userId, Pageable.unpaged())).thenReturn(page);
        assertEquals(1, reviewService.getReviewsByUser(userId, Pageable.unpaged()).getTotalElements());
    }

    @Test
    void getRatingSummaryMissingAndNullAverage() {
        when(lectureRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reviewService.getRatingSummary(1L));

        when(lectureRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.countByLectureId(1L)).thenReturn(2L);
        when(reviewRepository.findAverageRatingByLectureId(1L)).thenReturn(null);

        RatingSummaryResponse summary = reviewService.getRatingSummary(1L);
        assertEquals(0.0, summary.getAverageRating());
        assertEquals(2L, summary.getTotalReviews());
    }

    private static Lecture lecture(Long id) {
        Lecture lecture = new Lecture();
        lecture.setId(id);
        lecture.setTitle("Intro");
        return lecture;
    }

    private static Review review(Long id, UUID userId, Lecture lecture) {
        User user = new User();
        user.setId(userId);
        user.setName("U");
        Review review = new Review();
        review.setId(id);
        review.setRating(5);
        review.setComment("c");
        review.setUser(user);
        review.setLecture(lecture);
        return review;
    }
}
