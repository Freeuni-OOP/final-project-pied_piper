package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class FeedMapper {

    public FeedItemResponse toResponse(Activity activity) {
        return toResponse(activity, null);
    }

    public FeedItemResponse toResponse(Activity activity, Review review) {
        Review resolved = review != null ? review : activity.getReview();
        return new FeedItemResponse(
                activity.getId(),
                activity.getType(),
                activity.getUser().getId(),
                activity.getUser().getName(),
                activity.getLecture().getId(),
                activity.getLecture().getTitle(),
                activity.getReviewId() != null
                        ? activity.getReviewId()
                        : (resolved != null ? resolved.getId() : null),
                activity.getLectureLogId(),
                resolved != null ? resolved.getRating() : null,
                resolved != null ? resolved.getComment() : null,
                activity.getCreatedAt()
        );
    }

    public LectureLogResponse toResponse(LectureLog lectureLog) {
        return new LectureLogResponse(
                lectureLog.getId(),
                lectureLog.getUser().getId(),
                lectureLog.getLecture().getId(),
                lectureLog.getLecture().getTitle(),
                lectureLog.getWatchedAt(),
                lectureLog.getCreatedAt()
        );
    }
}
