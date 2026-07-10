package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ChatMessageRequest;
import com.lecturboxd.dto.request.DeleteUserRequest;
import com.lecturboxd.dto.request.FacultyCreateRequest;
import com.lecturboxd.dto.request.FacultyImportRequest;
import com.lecturboxd.dto.request.FacultyUpdateRequest;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.request.LectureRequest;
import com.lecturboxd.dto.request.LoginRequest;
import com.lecturboxd.dto.request.RegisterRequest;
import com.lecturboxd.dto.request.ReviewRequest;
import com.lecturboxd.dto.request.SemesterCreateRequest;
import com.lecturboxd.dto.request.SemesterUpdateRequest;
import com.lecturboxd.dto.request.StartConversationRequest;
import com.lecturboxd.dto.request.UpdateProfileRequest;
import com.lecturboxd.dto.request.VerifyOtpRequest;
import com.lecturboxd.dto.response.AuthResponse;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.dto.response.DevDeleteResponse;
import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.dto.response.FollowStatusResponse;
import com.lecturboxd.dto.response.FollowUserResponse;
import com.lecturboxd.dto.response.ImportSummaryResponse;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.dto.response.LectureResponse;
import com.lecturboxd.dto.response.RatingSummaryResponse;
import com.lecturboxd.dto.response.RegisterResponse;
import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.dto.response.SubjectSummaryResponse;
import com.lecturboxd.dto.response.SubjectSyllabusResponse;
import com.lecturboxd.dto.response.UserProfileResponse;
import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.service.AdminImportService;
import com.lecturboxd.service.AuthService;
import com.lecturboxd.service.ChatService;
import com.lecturboxd.service.FacultyService;
import com.lecturboxd.service.FeedService;
import com.lecturboxd.service.FollowService;
import com.lecturboxd.service.LectureLogService;
import com.lecturboxd.service.LectureService;
import com.lecturboxd.service.ReviewService;
import com.lecturboxd.service.SemesterService;
import com.lecturboxd.service.SyllabusService;
import com.lecturboxd.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllersDelegationTest {

    @Mock private AuthService authService;
    @Mock private UserService userService;
    @Mock private LectureService lectureService;
    @Mock private ReviewService reviewService;
    @Mock private LectureLogService lectureLogService;
    @Mock private FollowService followService;
    @Mock private FeedService feedService;
    @Mock private ChatService chatService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private SyllabusService syllabusService;
    @Mock private FacultyService facultyService;
    @Mock private SemesterService semesterService;
    @Mock private AdminImportService adminImportService;

    @InjectMocks private AuthController authController;
    @InjectMocks private UserController userController;
    @InjectMocks private LectureController lectureController;
    @InjectMocks private ReviewController reviewController;
    @InjectMocks private LectureLogController lectureLogController;
    @InjectMocks private FollowController followController;
    @InjectMocks private FeedController feedController;
    @InjectMocks private ChatController chatController;
    @InjectMocks private SyllabusController syllabusController;
    @InjectMocks private AdminFacultyController adminFacultyController;
    @InjectMocks private AdminSemesterController adminSemesterController;
    @InjectMocks private AdminImportController adminImportController;

    @Test
    void authControllerDelegates() {
        RegisterRequest register = new RegisterRequest();
        RegisterResponse registerResponse = new RegisterResponse();
        when(authService.register(register)).thenReturn(registerResponse);
        assertSame(registerResponse, authController.register(register));

        VerifyOtpRequest verify = new VerifyOtpRequest();
        AuthResponse auth = new AuthResponse("t", null);
        when(authService.verify(verify)).thenReturn(auth);
        assertSame(auth, authController.verify(verify));

        LoginRequest login = new LoginRequest();
        when(authService.login(login)).thenReturn(auth);
        assertSame(auth, authController.login(login));

        DeleteUserRequest delete = new DeleteUserRequest();
        DevDeleteResponse deleted = new DevDeleteResponse();
        when(authService.deleteUserForDev(delete)).thenReturn(deleted);
        assertSame(deleted, authController.deleteUser(delete));
    }

    @Test
    void userControllerDelegates() {
        LecturboxdUserPrincipal principal = principal();
        UserResponse user = new UserResponse(principal.getId(), "N", "n@x.com", true);
        when(userService.getCurrentUser(principal.getId())).thenReturn(user);
        assertSame(user, userController.getCurrentUser(principal));

        UpdateProfileRequest update = new UpdateProfileRequest();
        when(userService.updateCurrentUser(principal.getId(), update)).thenReturn(user);
        assertSame(user, userController.updateCurrentUser(principal, update));

        userController.deleteAccount(principal);
        verify(userService).deleteAccount(principal.getId());

        Page<UserResponse> page = new PageImpl<>(List.of(user));
        when(userService.searchUsers(eq("q"), any(Pageable.class))).thenReturn(page);
        assertSame(page, userController.searchUsers("q", Pageable.unpaged()));

        UserProfileResponse profile = new UserProfileResponse();
        when(userService.getUserProfile(principal.getId())).thenReturn(profile);
        assertSame(profile, userController.getUserProfile(principal.getId()));
    }

    @Test
    void lectureControllerDelegates() {
        LectureRequest request = new LectureRequest();
        LectureResponse response = new LectureResponse();
        when(lectureService.create(request)).thenReturn(response);
        assertSame(response, lectureController.create(request));

        Page<LectureResponse> page = new PageImpl<>(List.of(response));
        when(lectureService.search(eq("oop"), any(Pageable.class))).thenReturn(page);
        assertSame(page, lectureController.search("oop", Pageable.unpaged()));

        when(lectureService.getById(1L)).thenReturn(response);
        assertSame(response, lectureController.getById(1L));

        when(lectureService.getAll()).thenReturn(List.of(response));
        assertEquals(1, lectureController.getAll().size());

        when(lectureService.update(1L, request)).thenReturn(response);
        assertSame(response, lectureController.update(1L, request));

        lectureController.delete(1L);
        verify(lectureService).delete(1L);
    }

    @Test
    void reviewControllerDelegates() {
        LecturboxdUserPrincipal principal = principal();
        ReviewRequest request = new ReviewRequest();
        ReviewResponse response = new ReviewResponse();
        when(reviewService.createReview(principal.getId(), 1L, request)).thenReturn(response);
        assertSame(response, reviewController.createReview(principal, 1L, request));

        when(reviewService.updateReview(principal.getId(), 2L, request)).thenReturn(response);
        assertSame(response, reviewController.updateReview(principal, 2L, request));

        reviewController.deleteReview(principal, 2L);
        verify(reviewService).deleteReview(principal.getId(), 2L);

        Page<ReviewResponse> page = new PageImpl<>(List.of(response));
        when(reviewService.getReviewsForLecture(eq(1L), any(Pageable.class))).thenReturn(page);
        assertSame(page, reviewController.getReviewsForLecture(1L, Pageable.unpaged()));

        RatingSummaryResponse summary = new RatingSummaryResponse();
        when(reviewService.getRatingSummary(1L)).thenReturn(summary);
        assertSame(summary, reviewController.getRatingSummary(1L));

        when(reviewService.getReviewsByUser(eq(principal.getId()), any(Pageable.class))).thenReturn(page);
        assertSame(page, reviewController.getReviewsByUser(principal.getId(), Pageable.unpaged()));
    }

    @Test
    void lectureLogControllerDelegatesIncludingNullBody() {
        LecturboxdUserPrincipal principal = principal();
        LectureLogResponse response = new LectureLogResponse();
        when(lectureLogService.createLog(eq(principal.getId()), eq(1L), any(LectureLogRequest.class)))
                .thenReturn(response);
        assertSame(response, lectureLogController.createLog(principal, 1L, null));

        LectureLogRequest body = new LectureLogRequest();
        when(lectureLogService.createLog(principal.getId(), 1L, body)).thenReturn(response);
        assertSame(response, lectureLogController.createLog(principal, 1L, body));

        when(lectureLogService.getMyLog(principal.getId(), 1L)).thenReturn(response);
        assertSame(response, lectureLogController.getMyLog(principal, 1L));

        lectureLogController.deleteMyLog(principal, 1L);
        verify(lectureLogService).deleteMyLog(principal.getId(), 1L);

        Page<LectureLogResponse> page = new PageImpl<>(List.of(response));
        when(lectureLogService.getLogsByUser(eq(principal.getId()), any(Pageable.class))).thenReturn(page);
        assertSame(page, lectureLogController.getLogsByUser(principal.getId(), Pageable.unpaged()));

        lectureLogController.deleteLog(principal, 9L);
        verify(lectureLogService).deleteLog(principal.getId(), 9L);
    }

    @Test
    void followFeedSyllabusAdminDelegate() {
        LecturboxdUserPrincipal principal = principal();
        UUID other = UUID.randomUUID();
        FollowStatusResponse status = new FollowStatusResponse(other, true);
        when(followService.follow(principal.getId(), other)).thenReturn(status);
        assertSame(status, followController.follow(principal, other));
        when(followService.unfollow(principal.getId(), other)).thenReturn(status);
        assertSame(status, followController.unfollow(principal, other));
        when(followService.getFollowStatus(principal.getId(), other)).thenReturn(status);
        assertSame(status, followController.followStatus(principal, other));
        when(followService.getFollowers(other)).thenReturn(List.of(new FollowUserResponse()));
        assertEquals(1, followController.followers(other).size());
        when(followService.getFollowing(other)).thenReturn(List.of(new FollowUserResponse()));
        assertEquals(1, followController.following(other).size());

        Page<FeedItemResponse> feed = new PageImpl<>(List.of());
        when(feedService.getFeedForUser(eq(principal.getId()), any(Pageable.class))).thenReturn(feed);
        assertSame(feed, feedController.getFeed(principal, Pageable.unpaged()));

        when(syllabusService.listFaculties()).thenReturn(List.of(new FacultyResponse()));
        assertEquals(1, syllabusController.listFaculties().size());
        when(syllabusService.listSemesters(1L)).thenReturn(List.of(new SemesterResponse()));
        assertEquals(1, syllabusController.listSemesters(1L).size());
        when(syllabusService.listSubjects(2L)).thenReturn(List.of(new SubjectSummaryResponse()));
        assertEquals(1, syllabusController.listSubjects(2L).size());
        SubjectSyllabusResponse syllabus = new SubjectSyllabusResponse();
        when(syllabusService.getSubjectSyllabus(3L)).thenReturn(syllabus);
        assertSame(syllabus, syllabusController.getSubject(3L));

        FacultyCreateRequest create = new FacultyCreateRequest();
        FacultyResponse faculty = new FacultyResponse();
        when(facultyService.create(create)).thenReturn(faculty);
        assertSame(faculty, adminFacultyController.create(create));
        when(facultyService.findAll()).thenReturn(List.of(faculty));
        assertEquals(1, adminFacultyController.findAll().size());
        when(facultyService.findById(1L)).thenReturn(faculty);
        assertSame(faculty, adminFacultyController.findById(1L));
        FacultyUpdateRequest update = new FacultyUpdateRequest();
        when(facultyService.update(1L, update)).thenReturn(faculty);
        assertSame(faculty, adminFacultyController.update(1L, update));
        adminFacultyController.delete(1L);
        verify(facultyService).delete(1L);

        SemesterCreateRequest semesterCreate = new SemesterCreateRequest();
        SemesterResponse semester = new SemesterResponse();
        when(semesterService.create(1L, semesterCreate)).thenReturn(semester);
        assertSame(semester, adminSemesterController.create(1L, semesterCreate));
        when(semesterService.findByFacultyId(1L)).thenReturn(List.of(semester));
        assertEquals(1, adminSemesterController.findByFaculty(1L).size());
        SemesterUpdateRequest semesterUpdate = new SemesterUpdateRequest();
        when(semesterService.update(2L, semesterUpdate)).thenReturn(semester);
        assertSame(semester, adminSemesterController.update(2L, semesterUpdate));
        adminSemesterController.delete(2L);
        verify(semesterService).delete(2L);

        FacultyImportRequest importRequest = new FacultyImportRequest();
        ImportSummaryResponse summary = new ImportSummaryResponse();
        when(adminImportService.importFacultyData(importRequest)).thenReturn(summary);
        assertSame(summary, adminImportController.importFacultyData(importRequest));
    }

    @Test
    void chatControllerDelegatesAndPushes() {
        LecturboxdUserPrincipal principal = principal();
        ConversationResponse conversation = new ConversationResponse();
        when(chatService.getUserConversations(principal.getId())).thenReturn(List.of(conversation));
        assertEquals(1, chatController.getConversations(principal).size());

        StartConversationRequest start = new StartConversationRequest();
        UUID receiverId = UUID.randomUUID();
        start.setReceiverId(receiverId);
        when(chatService.startConversation(principal.getId(), receiverId)).thenReturn(conversation);
        assertSame(conversation, chatController.startConversation(principal, start));

        ChatMessageRequest messageRequest = new ChatMessageRequest();
        ChatMessageResponse.UserSummary sender =
                new ChatMessageResponse.UserSummary(principal.getId(), "Me", "me@freeuni.edu.ge");
        ChatMessageResponse.UserSummary receiver =
                new ChatMessageResponse.UserSummary(receiverId, "You", "you@freeuni.edu.ge");
        ChatMessageResponse message =
                new ChatMessageResponse(1L, "hi", sender, receiver, null, false);
        when(chatService.sendMessage(principal.getId(), messageRequest)).thenReturn(message);
        assertSame(message, chatController.sendMessage(principal, messageRequest));
        verify(messagingTemplate).convertAndSendToUser("you@freeuni.edu.ge", "/queue/messages", message);
        verify(messagingTemplate).convertAndSendToUser("me@freeuni.edu.ge", "/queue/messages", message);

        Page<ChatMessageResponse> page = new PageImpl<>(List.of(message));
        when(chatService.getChatHistory(eq(principal.getId()), eq(5L), any(Pageable.class))).thenReturn(page);
        assertSame(page, chatController.getChatHistory(principal, 5L, Pageable.unpaged()));
        assertSame(page, chatController.getChatHistoryLegacy(principal, 5L, Pageable.unpaged()));

        chatController.markConversationAsRead(principal, 5L);
        verify(chatService).markMessagesAsRead(principal.getId(), 5L);
        chatController.markMessageAsRead(principal, 9L);
        verify(chatService).markMessageAsRead(principal.getId(), 9L);
    }

    @Test
    void chatControllerSkipsPushWhenEmailsNull() {
        LecturboxdUserPrincipal principal = principal();
        ChatMessageRequest messageRequest = new ChatMessageRequest();
        ChatMessageResponse.UserSummary sender =
                new ChatMessageResponse.UserSummary(principal.getId(), "Me", null);
        ChatMessageResponse.UserSummary receiver =
                new ChatMessageResponse.UserSummary(UUID.randomUUID(), "You", null);
        ChatMessageResponse message =
                new ChatMessageResponse(1L, "hi", sender, receiver, null, false);
        when(chatService.sendMessage(principal.getId(), messageRequest)).thenReturn(message);

        chatController.sendMessage(principal, messageRequest);

        verify(messagingTemplate, org.mockito.Mockito.never())
                .convertAndSendToUser(any(), eq("/queue/messages"), any());
    }

    private static LecturboxdUserPrincipal principal() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("me@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(true);
        return new LecturboxdUserPrincipal(user);
    }
}
