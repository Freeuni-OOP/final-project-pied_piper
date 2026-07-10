package com.lecturboxd.service;

import com.lecturboxd.dto.response.FollowStatusResponse;
import com.lecturboxd.dto.response.FollowUserResponse;
import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.BadRequestException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceCoverageTest {

    @Mock private FollowRepository followRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    void followThrowsWhenUsersMissing() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        when(userRepository.findById(me)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> followService.follow(me, target));

        when(userRepository.findById(me)).thenReturn(Optional.of(user(me)));
        when(userRepository.findById(target)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> followService.follow(me, target));
    }

    @Test
    void unfollowRejectsSelfAndMissingTarget() {
        UUID me = UUID.randomUUID();
        assertThrows(BadRequestException.class, () -> followService.unfollow(me, me));

        UUID target = UUID.randomUUID();
        when(userRepository.existsById(target)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> followService.unfollow(me, target));
    }

    @Test
    void getFollowStatusTrueAndFalse() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        when(userRepository.existsById(target)).thenReturn(true);
        when(followRepository.existsByFollowerIdAndFollowedId(me, target)).thenReturn(true);
        FollowStatusResponse yes = followService.getFollowStatus(me, target);
        assertTrue(yes.isFollowing());

        when(followRepository.existsByFollowerIdAndFollowedId(me, target)).thenReturn(false);
        FollowStatusResponse no = followService.getFollowStatus(me, target);
        assertFalse(no.isFollowing());
    }

    @Test
    void getFollowersAndFollowing() {
        UUID userId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> followService.getFollowers(userId));
        assertThrows(ResourceNotFoundException.class, () -> followService.getFollowing(userId));

        when(userRepository.existsById(userId)).thenReturn(true);
        Follow follow = new Follow();
        follow.setFollower(user(otherId, "Other", "o@freeuni.edu.ge"));
        follow.setFollowed(user(userId));
        when(followRepository.findByFollowedIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(follow));
        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(follow));

        List<FollowUserResponse> followers = followService.getFollowers(userId);
        assertEquals(1, followers.size());
        assertEquals(otherId, followers.get(0).getId());

        // For following, map uses getFollowed — reuse same follow object with followed = other
        Follow following = new Follow();
        following.setFollower(user(userId));
        following.setFollowed(user(otherId, "Other", "o@freeuni.edu.ge"));
        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(following));
        List<FollowUserResponse> followingList = followService.getFollowing(userId);
        assertEquals(otherId, followingList.get(0).getId());
        assertEquals("Other", followingList.get(0).getName());
    }

    private static User user(UUID id) {
        return user(id, "U", id + "@freeuni.edu.ge");
    }

    private static User user(UUID id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
