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

/**
 * EN: Manages user profile reads/updates, search, and full account deletion with FK-safe cascade order.
 * KA: მართავს პროფილის წაკითხვას/განახლებას, ძებნას და ანგარიშის სრულ წაშლას FK-უსაფრთხო კასკადური რიგით.
 */
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

    /**
     * EN: Returns the authenticated user's basic profile DTO.
     * KA: აბრუნებს ავთენტიფიცირებული მომხმარებლის ძირითად პროფილის DTO-ს.
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        // EN: Load user from DB | KA: მომხმარებლის ჩატვირთვა ბაზიდან
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        return userMapper.toResponse(user);
    }

    /**
     * EN: Updates the authenticated user's display name.
     * KA: განაახლებს ავთენტიფიცირებული მომხმარებლის სახელს.
     */
    @Transactional
    public UserResponse updateCurrentUser(UUID userId, UpdateProfileRequest request) {
        // EN: Load user from DB | KA: მომხმარებლის ჩატვირთვა ბაზიდან
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        // EN: Persist updated name | KA: განახლებული სახელის შენახვა
        user.setName(request.getName().trim());
        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * EN: Returns a public profile with follower, following, review, and log counts.
     * KA: აბრუნებს საჯარო პროფილს გამომწერების, გამოწერილების, რევიუებისა და ლოგების რაოდენობით.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        // EN: Load user from DB | KA: მომხმარებლის ჩატვირთვა ბაზიდან
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // EN: Aggregate related counts from DB | KA: დაკავშირებული რაოდენობების აგრეგაცია ბაზიდან
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

    /**
     * EN: Searches users by name or email with pagination.
     * KA: ეძებს მომხმარებლებს სახელით ან ელფოსტით გვერდებად დაყოფით.
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        // EN: Empty query returns empty page | KA: ცარიელი მოთხოვნა აბრუნებს ცარიელ გვერდს
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return Page.empty(pageable);
        }
        // EN: DB search by name or email | KA: ბაზაში ძებნა სახელით ან ელფოსტით
        return userRepository.searchByNameOrEmail(trimmed, pageable).map(userMapper::toResponse);
    }

    /**
     * EN: Permanently deletes a user and all dependent data in FK-safe order.
     * KA: სამუდამოდ შლის მომხმარებელს და ყველა დამოკიდებულ მონაცემს FK-უსაფრთხო რიგით.
     */
    @Transactional
    public void deleteAccount(UUID userId) {
        // EN: Load user from DB | KA: მომხმარებლის ჩატვირთვა ბაზიდან
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // EN: Cascade delete order (FK-safe): activities → reviews/logs → follows → messages → conversations → OTP codes → user
        // KA: კასკადური წაშლის რიგი (FK-უსაფრთხო): აქტივობები → რევიუები/ლოგები → გამოწერები → შეტყობინებები → საუბრები → OTP კოდები → მომხმარებელი
        activityRepository.deleteByUserId(userId);
        reviewRepository.deleteByUserId(userId);
        lectureLogRepository.deleteByUserId(userId);
        followRepository.deleteAllForUser(userId);
        chatMessageRepository.deleteAllForUser(userId);
        conversationRepository.deleteAllForUser(userId);
        verificationCodeRepository.deleteAllByEmail(user.getEmail().toLowerCase(Locale.ROOT));
        userRepository.delete(user);
        userRepository.flush();
    }
}
