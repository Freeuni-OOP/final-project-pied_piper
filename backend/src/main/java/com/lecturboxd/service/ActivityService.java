package com.lecturboxd.service;

import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import com.lecturboxd.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EN: Records user activity events used by the social feed.
 * KA: იწერს მომხმარებლის აქტივობის მოვლენებს, რომლებიც სოციალურ ფიდში გამოიყენება.
 */
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * EN: Persists a REVIEW_CREATED activity linked to the given review.
     * KA: ინახავს REVIEW_CREATED აქტივობას მოცემულ რევიუსთან დაკავშირებით.
     */
    @Transactional
    public void recordReviewCreated(Review review) {
        // EN: Build activity from review fields | KA: აქტივობის აგება რევიუს ველებიდან
        Activity activity = new Activity();
        activity.setUser(review.getUser());
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setLecture(review.getLecture());
        activity.setReview(review);
        // EN: Side effect — save activity for feed | KA: გვერდითი ეფექტი — აქტივობის შენახვა ფიდისთვის
        activityRepository.save(activity);
    }

    /**
     * EN: Persists a LECTURE_LOGGED activity linked to the given lecture log.
     * KA: ინახავს LECTURE_LOGGED აქტივობას მოცემულ ლექციის ლოგთან დაკავშირებით.
     */
    @Transactional
    public void recordLectureLogged(LectureLog lectureLog) {
        // EN: Build activity from lecture log fields | KA: აქტივობის აგება ლექციის ლოგის ველებიდან
        Activity activity = new Activity();
        activity.setUser(lectureLog.getUser());
        activity.setType(ActivityType.LECTURE_LOGGED);
        activity.setLecture(lectureLog.getLecture());
        activity.setLectureLogId(lectureLog.getId());
        // EN: Side effect — save activity for feed | KA: გვერდითი ეფექტი — აქტივობის შენახვა ფიდისთვის
        activityRepository.save(activity);
    }
}
