package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.UserMapper;
import com.lecturboxd.dto.request.UpdateProfileRequest;
import com.lecturboxd.dto.response.UserProfileResponse;
import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final LectureLogRepository lectureLogRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            FollowRepository followRepository,
            ReviewRepository reviewRepository,
            LectureLogRepository lectureLogRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.reviewRepository = reviewRepository;
        this.lectureLogRepository = lectureLogRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        user.setName(request.getName().trim());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                followRepository.countByFollowedId(userId),
                followRepository.countByFollowerId(userId),
                reviewRepository.countByUserId(userId),
                lectureLogRepository.countByUserId(userId)
        );
    }
}
