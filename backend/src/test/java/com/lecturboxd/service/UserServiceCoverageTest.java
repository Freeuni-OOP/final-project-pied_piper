package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.UserMapper;
import com.lecturboxd.dto.request.UpdateProfileRequest;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceCoverageTest {

    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private LectureLogRepository lectureLogRepository;
    @Mock private ActivityRepository activityRepository;
    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private VerificationCodeRepository verificationCodeRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserPaths() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser(id));

        User user = user(id);
        UserResponse mapped = new UserResponse(id, "N", "n@freeuni.edu.ge", true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(mapped);
        assertEquals(mapped, userService.getCurrentUser(id));
    }

    @Test
    void updateCurrentUserThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("X");
        assertThrows(ResourceNotFoundException.class, () -> userService.updateCurrentUser(id, request));
    }

    @Test
    void getUserProfileThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserProfile(id));
    }

    @Test
    void searchUsersNullAndDelegates() {
        assertTrue(userService.searchUsers(null, Pageable.unpaged()).isEmpty());

        User user = user(UUID.randomUUID());
        UserResponse mapped = new UserResponse(user.getId(), "N", user.getEmail(), true);
        when(userRepository.searchByNameOrEmail("nika", Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toResponse(user)).thenReturn(mapped);

        assertEquals(1, userService.searchUsers("nika", Pageable.unpaged()).getTotalElements());
    }

    private static User user(UUID id) {
        User user = new User();
        user.setId(id);
        user.setName("N");
        user.setEmail("n@freeuni.edu.ge");
        user.setVerified(true);
        return user;
    }
}
