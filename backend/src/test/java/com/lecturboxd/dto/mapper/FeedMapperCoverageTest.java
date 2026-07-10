package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedMapperCoverageTest {

    private final FeedMapper feedMapper = new FeedMapper();

    @Test
    void toResponseMapsLectureLog() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        Lecture lecture = new Lecture();
        lecture.setId(4L);
        lecture.setTitle("Intro");

        LectureLog log = new LectureLog();
        log.setId(8L);
        log.setUser(user);
        log.setLecture(lecture);
        log.setWatchedAt(LocalDate.of(2026, 1, 2));
        log.setCreatedAt(LocalDateTime.of(2026, 1, 3, 10, 0));

        LectureLogResponse response = feedMapper.toResponse(log);

        assertEquals(8L, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(4L, response.getLectureId());
        assertEquals("Intro", response.getLectureTitle());
        assertEquals(LocalDate.of(2026, 1, 2), response.getWatchedAt());
    }

    @Test
    void toResponsePrefersActivityReviewId() {
        Activity activity = baseActivity();
        Review activityReview = new Review();
        activityReview.setId(42L);
        activityReview.setRating(1);
        activityReview.setComment("activity");
        activity.setReview(activityReview);

        Review review = new Review();
        review.setId(99L);
        review.setRating(4);
        review.setComment("from param");

        FeedItemResponse response = feedMapper.toResponse(activity, review);
        assertEquals(42L, response.getReviewId());
        assertEquals(4, response.getRating());
        assertEquals("from param", response.getComment());
    }

    @Test
    void toResponseFallsBackToActivityReview() {
        Activity activity = baseActivity();
        Review linked = new Review();
        linked.setId(7L);
        linked.setRating(2);
        linked.setComment("linked");
        activity.setReview(linked);

        FeedItemResponse response = feedMapper.toResponse(activity, null);
        assertEquals(7L, response.getReviewId());
        assertEquals(2, response.getRating());
        assertEquals("linked", response.getComment());
    }

    @Test
    void toResponseWithNoReviewAnywhere() {
        Activity activity = baseActivity();
        FeedItemResponse response = feedMapper.toResponse(activity, null);
        assertNull(response.getReviewId());
        assertNull(response.getRating());
        assertNull(response.getComment());
    }

    private static Activity baseActivity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("A");
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("L");
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setUser(user);
        activity.setLecture(lecture);
        activity.setCreatedAt(LocalDateTime.now());
        return activity;
    }
}
