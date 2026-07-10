package com.lecturboxd.service;

import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void recordReviewCreatedStoresReviewLink() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        Review review = new Review();
        review.setId(10L);
        review.setUser(user);
        review.setLecture(lecture);

        activityService.recordReviewCreated(review);

        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(captor.capture());
        assertEquals(ActivityType.REVIEW_CREATED, captor.getValue().getType());
        assertEquals(review, captor.getValue().getReview());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(lecture, captor.getValue().getLecture());
    }

    @Test
    void recordLectureLoggedStoresLogId() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Lecture lecture = new Lecture();
        lecture.setId(2L);
        LectureLog log = new LectureLog();
        log.setId(20L);
        log.setUser(user);
        log.setLecture(lecture);

        activityService.recordLectureLogged(log);

        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(captor.capture());
        assertEquals(ActivityType.LECTURE_LOGGED, captor.getValue().getType());
        assertEquals(20L, captor.getValue().getLectureLogId());
    }
}
