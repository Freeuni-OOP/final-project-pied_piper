package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.LectureLog;
import org.springframework.stereotype.Component;

@Component
public class FeedMapper {

    public FeedItemResponse toResponse(Activity activity) {
        return new FeedItemResponse(
                activity.getId(),
                activity.getType(),
                activity.getUser().getId(),
                activity.getUser().getName(),
                activity.getLecture().getId(),
                activity.getLecture().getTitle(),
                activity.getReviewId(),
                activity.getLectureLogId(),
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
