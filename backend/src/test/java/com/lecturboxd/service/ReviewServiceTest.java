package com.lecturboxd.service;

import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReviewThrowsConflictWhenReviewAlreadyExists() {
        UUID userId = UUID.randomUUID();
        Long lectureId = 1L;
        ReviewRequest request = new ReviewRequest();
        request.setRating(5);
        request.setComment("Excellent");

        Lecture lecture = new Lecture();
        lecture.setId(lectureId);

        User user = new User();
        user.setId(userId);

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndLectureId(userId, lectureId)).thenReturn(Optional.of(new Review()));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reviewService.createReview(userId, lectureId, request)
        );

        assertEquals("A review for this lecture already exists. Use the update endpoint to modify it.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }
}
