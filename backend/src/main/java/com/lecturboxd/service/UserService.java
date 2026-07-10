package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.UserMapper;
import com.lecturboxd.dto.request.UpdateProfileRequest;
import com.lecturboxd.dto.response.UserProfileResponse;
import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.ActivityRepository;
import com.lecturboxd.repository.ChatMessageRepository;
import com.lecturboxd.repository.ConversationRepository;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.ReviewRepository;
import com.lecturboxd.repository.UserRepository;
import com.lecturboxd.repository.VerificationCodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final LectureLogRepository lectureLogRepository;
    private final ActivityRepository activityRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            FollowRepository followRepository,
            ReviewRepository reviewRepository,
            LectureLogRepository lectureLogRepository,
            ActivityRepository activityRepository,
            ChatMessageRepository chatMessageRepository,
            ConversationRepository conversationRepository,
            VerificationCodeRepository verificationCodeRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.reviewRepository = reviewRepository;
        this.lectureLogRepository = lectureLogRepository;
        this.activityRepository = activityRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.conversationRepository = conversationRepository;
        this.verificationCodeRepository = verificationCodeRepository;
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

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return Page.empty(pageable);
        }
        return userRepository.searchByNameOrEmail(trimmed, pageable).map(userMapper::toResponse);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        activityRepository.deleteByUserId(userId);
        reviewRepository.deleteByUserId(userId);
        lectureLogRepository.deleteByUserId(userId);
        followRepository.deleteAllForUser(userId);
        chatMessageRepository.deleteAllForUser(userId);
        conversationRepository.deleteAllForUser(userId);
        verificationCodeRepository.deleteAllByEmail(user.getEmail().toLowerCase(Locale.ROOT));
        userRepository.delete(user);
    }
}
