package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedMapperTest {

    private final FeedMapper feedMapper = new FeedMapper();

    @Test
    void toResponseIncludesReviewFields() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Mate");

        Lecture lecture = new Lecture();
        lecture.setId(50L);
        lecture.setTitle("Intro");

        Review review = new Review();
        review.setId(6L);
        review.setRating(5);
        review.setComment("nice");

        Activity activity = new Activity();
        activity.setId(1L);
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setUser(user);
        activity.setLecture(lecture);
        activity.setReview(review);
        activity.setCreatedAt(LocalDateTime.of(2026, 7, 10, 12, 0));

        FeedItemResponse response = feedMapper.toResponse(activity, review);

        assertEquals(1L, response.getId());
        assertEquals(ActivityType.REVIEW_CREATED, response.getType());
        assertEquals(userId, response.getActorId());
        assertEquals("Mate", response.getActorName());
        assertEquals(50L, response.getLectureId());
        assertEquals(6L, response.getReviewId());
        assertEquals(5, response.getRating());
        assertEquals("nice", response.getComment());
    }

    @Test
    void toResponseWithoutReviewLeavesRatingNull() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("X");
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("L");

        Activity activity = new Activity();
        activity.setId(2L);
        activity.setType(ActivityType.LECTURE_LOGGED);
        activity.setUser(user);
        activity.setLecture(lecture);
        activity.setLectureLogId(9L);
        activity.setCreatedAt(LocalDateTime.now());

        FeedItemResponse response = feedMapper.toResponse(activity);

        assertNull(response.getRating());
        assertNull(response.getComment());
        assertEquals(9L, response.getLectureLogId());
    }
}
