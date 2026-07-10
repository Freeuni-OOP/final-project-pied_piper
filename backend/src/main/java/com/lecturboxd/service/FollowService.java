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

/**
 * EN: Manages follow relationships between users (follow, unfollow, status, lists).
 * KA: მართავს მომხმარებლებს შორის გამოწერის ურთიერთობებს (გამოწერა, გაუქმება, სტატუსი, სიები).
 */
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    /**
     * EN: Creates a follow edge from the current user to the target user if it does not already exist.
     * KA: ქმნის გამოწერის კავშირს მიმდინარე მომხმარებლიდან სამიზნე მომხმარებელზე, თუ ის ჯერ არ არსებობს.
     */
    @Transactional
    public FollowStatusResponse follow(UUID currentUserId, UUID targetUserId) {
        // EN: Reject self-follow | KA: საკუთარი თავის გამოწერა აკრძალულია
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        // EN: Load both users from DB | KA: ორივე მომხმარებლის ჩატვირთვა ბაზიდან
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + currentUserId));
        User followed = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + targetUserId));

        // EN: Persist follow only if missing (idempotent) | KA: გამოწერის შენახვა მხოლოდ თუ არ არსებობს (იდემპოტენტური)
        if (!followRepository.existsByFollowerAndFollowed(follower, followed)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);
        }

        return new FollowStatusResponse(targetUserId, true);
    }

    /**
     * EN: Removes the follow edge from the current user to the target user.
     * KA: შლის გამოწერის კავშირს მიმდინარე მომხმარებლიდან სამიზნე მომხმარებელზე.
     */
    @Transactional
    public FollowStatusResponse unfollow(UUID currentUserId, UUID targetUserId) {
        // EN: Reject self-unfollow | KA: საკუთარი თავის გამოწერის გაუქმება აკრძალულია
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot unfollow yourself");
        }
        // EN: Ensure target user exists | KA: სამიზნე მომხმარებლის არსებობის შემოწმება
        ensureUserExists(targetUserId);
        // EN: Delete follow row by IDs | KA: გამოწერის ჩანაწერის წაშლა ID-ებით
        followRepository.deleteByFollowerIdAndFollowedId(currentUserId, targetUserId);
        return new FollowStatusResponse(targetUserId, false);
    }

    /**
     * EN: Returns whether the current user follows the target user.
     * KA: აბრუნებს, მიჰყვება თუ არა მიმდინარე მომხმარებელი სამიზნე მომხმარებელს.
     */
    @Transactional(readOnly = true)
    public FollowStatusResponse getFollowStatus(UUID currentUserId, UUID targetUserId) {
        // EN: Ensure target user exists | KA: სამიზნე მომხმარებლის არსებობის შემოწმება
        ensureUserExists(targetUserId);
        // EN: Check follow existence in DB | KA: გამოწერის არსებობის შემოწმება ბაზაში
        boolean following = followRepository.existsByFollowerIdAndFollowedId(currentUserId, targetUserId);
        return new FollowStatusResponse(targetUserId, following);
    }

    /**
     * EN: Lists users who follow the given user, newest first.
     * KA: აბრუნებს მოცემული მომხმარებლის გამომწერების სიას, უახლესი პირველი.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowers(UUID userId) {
        // EN: Ensure user exists | KA: მომხმარებლის არსებობის შემოწმება
        ensureUserExists(userId);
        // EN: Map follower entities to response DTOs | KA: გამომწერი ენთითების მაპინგი პასუხის DTO-ებზე
        return followRepository.findByFollowedIdOrderByCreatedAtDesc(userId).stream()
                .map(Follow::getFollower)
                .map(this::toFollowUser)
                .toList();
    }

    /**
     * EN: Lists users that the given user follows, newest first.
     * KA: აბრუნებს მომხმარებლებს, რომლებსაც მოცემული მომხმარებელი მიჰყვება, უახლესი პირველი.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowing(UUID userId) {
        // EN: Ensure user exists | KA: მომხმარებლის არსებობის შემოწმება
        ensureUserExists(userId);
        // EN: Map followed entities to response DTOs | KA: გამოწერილი ენთითების მაპინგი პასუხის DTO-ებზე
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
