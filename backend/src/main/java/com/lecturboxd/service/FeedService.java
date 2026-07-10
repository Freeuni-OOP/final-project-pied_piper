package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.repository.ActivityRepository;
import com.lecturboxd.repository.FollowRepository;
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
    private final FeedMapper feedMapper;

    public FeedService(
            ActivityRepository activityRepository,
            FollowRepository followRepository,
            FeedMapper feedMapper
    ) {
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
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
                .map(feedMapper::toResponse);
    }
}
