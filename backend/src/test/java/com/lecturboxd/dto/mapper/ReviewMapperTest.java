package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewMapperTest {

    @Test
    void toResponseMapsNestedSummaries() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Mate");

        Lecture lecture = new Lecture();
        lecture.setId(20L);
        lecture.setTitle("Intro");

        Review review = new Review();
        review.setId(7L);
        review.setRating(4);
        review.setComment("good");
        review.setUser(user);
        review.setLecture(lecture);
        review.setCreatedAt(LocalDateTime.of(2026, 3, 1, 9, 0));
        review.setUpdatedAt(LocalDateTime.of(2026, 3, 2, 9, 0));

        ReviewResponse response = ReviewMapper.toResponse(review);

        assertEquals(7L, response.getId());
        assertEquals(4, response.getRating());
        assertEquals("good", response.getComment());
        assertEquals(userId, response.getAuthor().getId());
        assertEquals("Mate", response.getAuthor().getName());
        assertEquals(20L, response.getLecture().getId());
        assertEquals("Intro", response.getLecture().getTitle());
    }
}
