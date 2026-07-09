package com.lecturboxd.service;

import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import com.lecturboxd.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void recordReviewCreated(Review review) {
        Activity activity = new Activity();
        activity.setUser(review.getUser());
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setLecture(review.getLecture());
        activity.setReviewId(review.getId());
        activityRepository.save(activity);
    }

    @Transactional
    public void recordLectureLogged(LectureLog lectureLog) {
        Activity activity = new Activity();
        activity.setUser(lectureLog.getUser());
        activity.setType(ActivityType.LECTURE_LOGGED);
        activity.setLecture(lectureLog.getLecture());
        activity.setLectureLogId(lectureLog.getId());
        activityRepository.save(activity);
    }
}
