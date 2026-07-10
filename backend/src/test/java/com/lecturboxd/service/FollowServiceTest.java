package com.lecturboxd.service;

import com.lecturboxd.dto.response.FollowStatusResponse;
import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.BadRequestException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    void followRejectsSelf() {
        UUID id = UUID.randomUUID();
        assertThrows(BadRequestException.class, () -> followService.follow(id, id));
    }

    @Test
    void followCreatesRowWhenMissing() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        User follower = user(me);
        User followed = user(target);

        when(userRepository.findById(me)).thenReturn(Optional.of(follower));
        when(userRepository.findById(target)).thenReturn(Optional.of(followed));
        when(followRepository.existsByFollowerAndFollowed(follower, followed)).thenReturn(false);

        FollowStatusResponse status = followService.follow(me, target);

        assertTrue(status.isFollowing());
        ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(captor.capture());
        assertEquals(follower, captor.getValue().getFollower());
        assertEquals(followed, captor.getValue().getFollowed());
    }

    @Test
    void followIsIdempotentWhenAlreadyFollowing() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        User follower = user(me);
        User followed = user(target);

        when(userRepository.findById(me)).thenReturn(Optional.of(follower));
        when(userRepository.findById(target)).thenReturn(Optional.of(followed));
        when(followRepository.existsByFollowerAndFollowed(follower, followed)).thenReturn(true);

        FollowStatusResponse status = followService.follow(me, target);

        assertTrue(status.isFollowing());
        verify(followRepository, never()).save(any());
    }

    @Test
    void unfollowDeletesPair() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        when(userRepository.existsById(target)).thenReturn(true);

        FollowStatusResponse status = followService.unfollow(me, target);

        assertFalse(status.isFollowing());
        verify(followRepository).deleteByFollowerIdAndFollowedId(me, target);
    }

    @Test
    void getFollowStatusThrowsWhenTargetMissing() {
        UUID me = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        when(userRepository.existsById(target)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> followService.getFollowStatus(me, target));
    }

    private static User user(UUID id) {
        User user = new User();
        user.setId(id);
        user.setName("User");
        user.setEmail(id + "@freeuni.edu.ge");
        return user;
    }
}
