package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceExtendedTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReviewPersistsAndRecordsActivity() {
        UUID userId = UUID.randomUUID();
        Long lectureId = 1L;
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setTitle("L1");
        User user = new User();
        user.setId(userId);
        user.setName("Mate");

        ReviewRequest request = new ReviewRequest();
        request.setRating(4);
        request.setComment("solid");

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndLectureId(userId, lectureId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(42L);
            return r;
        });

        ReviewResponse response = reviewService.createReview(userId, lectureId, request);

        assertEquals(42L, response.getId());
        assertEquals(4, response.getRating());
        assertEquals("solid", response.getComment());
        verify(activityService).recordReviewCreated(any(Review.class));
    }

    @Test
    void getRatingSummaryUsesAverageWhenReviewsExist() {
        when(lectureRepository.existsById(10L)).thenReturn(true);
        when(reviewRepository.countByLectureId(10L)).thenReturn(2L);
        when(reviewRepository.findAverageRatingByLectureId(10L)).thenReturn(4.5);

        RatingSummaryResponse summary = reviewService.getRatingSummary(10L);

        assertEquals(10L, summary.getLectureId());
        assertEquals(4.5, summary.getAverageRating());
        assertEquals(2L, summary.getTotalReviews());
    }

    @Test
    void getRatingSummaryReturnsZeroWhenNoReviews() {
        when(lectureRepository.existsById(10L)).thenReturn(true);
        when(reviewRepository.countByLectureId(10L)).thenReturn(0L);

        RatingSummaryResponse summary = reviewService.getRatingSummary(10L);

        assertEquals(0.0, summary.getAverageRating());
        assertEquals(0L, summary.getTotalReviews());
        verify(reviewRepository, never()).findAverageRatingByLectureId(any());
    }

    @Test
    void updateReviewForbiddenForNonOwner() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        User user = new User();
        user.setId(owner);
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);

        ReviewRequest request = new ReviewRequest();
        request.setRating(3);
        request.setComment("x");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class,
                () -> reviewService.updateReview(other, 1L, request));
    }
}
