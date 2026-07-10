package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.Review;
import com.lecturboxd.repository.ActivityRepository;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedService {

    private final ActivityRepository activityRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final FeedMapper feedMapper;

    public FeedService(
            ActivityRepository activityRepository,
            FollowRepository followRepository,
            ReviewRepository reviewRepository,
            FeedMapper feedMapper
    ) {
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
        this.reviewRepository = reviewRepository;
        this.feedMapper = feedMapper;
    }

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getFeedForUser(UUID currentUserId, Pageable pageable) {
        List<UUID> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        followRepository.findByFollowerIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(follow -> follow.getFollowed().getId())
                .forEach(userIds::add);

        return activityRepository.findByUserIdIn(userIds, pageable)
                .map(activity -> feedMapper.toResponse(activity, resolveReview(activity)));
    }

    private Review resolveReview(Activity activity) {
        if (activity.getType() != ActivityType.REVIEW_CREATED) {
            return null;
        }
        if (activity.getReview() != null) {
            return activity.getReview();
        }
        // Fallback for older activity rows that may not have review_id set.
        return reviewRepository
                .findByUserIdAndLectureId(activity.getUser().getId(), activity.getLecture().getId())
                .orElse(null);
    }
}
