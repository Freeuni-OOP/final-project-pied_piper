package com.lecturboxd.service;

import com.lecturboxd.dto.response.FollowStatusResponse;
import com.lecturboxd.dto.response.FollowUserResponse;
import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.BadRequestException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FollowStatusResponse follow(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + currentUserId));
        User followed = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + targetUserId));

        if (!followRepository.existsByFollowerAndFollowed(follower, followed)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);
        }

        return new FollowStatusResponse(targetUserId, true);
    }

    @Transactional
    public FollowStatusResponse unfollow(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot unfollow yourself");
        }
        ensureUserExists(targetUserId);
        followRepository.deleteByFollowerIdAndFollowedId(currentUserId, targetUserId);
        return new FollowStatusResponse(targetUserId, false);
    }

    @Transactional(readOnly = true)
    public FollowStatusResponse getFollowStatus(UUID currentUserId, UUID targetUserId) {
        ensureUserExists(targetUserId);
        boolean following = followRepository.existsByFollowerIdAndFollowedId(currentUserId, targetUserId);
        return new FollowStatusResponse(targetUserId, following);
    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowers(UUID userId) {
        ensureUserExists(userId);
        return followRepository.findByFollowedIdOrderByCreatedAtDesc(userId).stream()
                .map(Follow::getFollower)
                .map(this::toFollowUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowing(UUID userId) {
        ensureUserExists(userId);
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId).stream()
                .map(Follow::getFollowed)
                .map(this::toFollowUser)
                .toList();
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
    }

    private FollowUserResponse toFollowUser(User user) {
        return new FollowUserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
