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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private LectureLogRepository lectureLogRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserProfileAggregatesCounts() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Mate");
        user.setEmail("mate@freeuni.edu.ge");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(followRepository.countByFollowedId(id)).thenReturn(3L);
        when(followRepository.countByFollowerId(id)).thenReturn(2L);
        when(reviewRepository.countByUserId(id)).thenReturn(5L);
        when(lectureLogRepository.countByUserId(id)).thenReturn(4L);

        UserProfileResponse profile = userService.getUserProfile(id);

        assertEquals("Mate", profile.getName());
        assertEquals(3L, profile.getFollowerCount());
        assertEquals(2L, profile.getFollowingCount());
        assertEquals(5L, profile.getReviewCount());
        assertEquals(4L, profile.getLectureLogCount());
    }

    @Test
    void searchUsersReturnsEmptyPageForBlankQuery() {
        Page<UserResponse> page = userService.searchUsers("   ", PageRequest.of(0, 10));
        assertTrue(page.isEmpty());
    }

    @Test
    void updateCurrentUserTrimsName() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Old");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("  New Name  ");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(id, "New Name", "a@b.c", true));

        UserResponse response = userService.updateCurrentUser(id, request);

        assertEquals("New Name", user.getName());
        assertEquals("New Name", response.getName());
    }

    @Test
    void deleteAccountRemovesRelatedDataInOrder() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setEmail("Mate@FreeUni.edu.ge");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteAccount(id);

        InOrder order = inOrder(
                activityRepository,
                reviewRepository,
                lectureLogRepository,
                followRepository,
                chatMessageRepository,
                conversationRepository,
                verificationCodeRepository,
                userRepository
        );
        order.verify(activityRepository).deleteByUserId(id);
        order.verify(reviewRepository).deleteByUserId(id);
        order.verify(lectureLogRepository).deleteByUserId(id);
        order.verify(followRepository).deleteAllForUser(id);
        order.verify(chatMessageRepository).deleteAllForUser(id);
        order.verify(conversationRepository).deleteAllForUser(id);
        order.verify(verificationCodeRepository).deleteAllByEmail("mate@freeuni.edu.ge");
        order.verify(userRepository).delete(user);
        order.verify(userRepository).flush();
    }

    @Test
    void deleteAccountThrowsWhenUserMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteAccount(id));
    }
}
