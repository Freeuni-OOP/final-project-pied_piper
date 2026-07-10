package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.Review;
import org.springframework.stereotype.Component;

/**
 * EN: Maps activity feed and lecture-log entities to feed-related response DTOs.
 * KA: აქტივობის ფიდისა და ლექციის ლოგის ერთეულებს ფიდთან დაკავშირებულ პასუხის DTO-ებად გარდაქმნის.
 */
@Component
public class FeedMapper {

    /**
     * EN: Maps an Activity to a FeedItemResponse using the activity's linked review when present.
     * KA: Activity-ს FeedItemResponse-ად გარდაქმნის აქტივობასთან დაკავშირებული მიმოხილვის გამოყენებით (თუ არსებობს).
     */
    public FeedItemResponse toResponse(Activity activity) {
        return toResponse(activity, null);
    }

    /**
     * EN: Maps an Activity to a FeedItemResponse, optionally using a preloaded Review to avoid lazy loads.
     * KA: Activity-ს FeedItemResponse-ად გარდაქმნის; სურვილისამებრ იყენებს წინასწარ ჩატვირთულ Review-ს lazy load-ის თავიდან ასაცილებლად.
     */
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

    /**
     * EN: Maps a LectureLog entity to a LectureLogResponse DTO.
     * KA: LectureLog ერთეულს LectureLogResponse DTO-ად გარდაქმნის.
     */
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
