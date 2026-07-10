package com.lecturboxd.websocket;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ChatMessageRequest;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.service.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock
    private ChatService chatService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatWebSocketController controller;

    @Test
    void sendMessageRequiresAuthentication() {
        ChatMessageRequest request = new ChatMessageRequest();
        controller.sendMessage(request, null);
        verify(chatService, never()).sendMessage(any(), any());
        verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    void sendMessagePushesToSenderAndReceiver() {
        LecturboxdUserPrincipal principal = principal();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        ChatMessageRequest request = new ChatMessageRequest();
        ChatMessageResponse response = message("sender@freeuni.edu.ge", "recv@freeuni.edu.ge");
        when(chatService.sendMessage(eq(principal.getId()), eq(request))).thenReturn(response);

        controller.sendMessage(request, auth);

        verify(messagingTemplate).convertAndSendToUser("recv@freeuni.edu.ge", "/queue/messages", response);
        verify(messagingTemplate).convertAndSendToUser("sender@freeuni.edu.ge", "/queue/messages", response);
    }

    @Test
    void sendMessageSkipsNullEmails() {
        LecturboxdUserPrincipal principal = principal();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        ChatMessageRequest request = new ChatMessageRequest();
        ChatMessageResponse response = message(null, null);
        when(chatService.sendMessage(eq(principal.getId()), eq(request))).thenReturn(response);

        controller.sendMessage(request, auth);

        verify(messagingTemplate, never()).convertAndSendToUser(any(), eq("/queue/messages"), any());
    }

    @Test
    void sendMessageHandlesNotFound() {
        LecturboxdUserPrincipal principal = principal();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        ChatMessageRequest request = new ChatMessageRequest();
        when(chatService.sendMessage(any(), any())).thenThrow(new ResourceNotFoundException("missing"));

        controller.sendMessage(request, auth);

        ArgumentCaptor<ChatWebSocketController.ErrorMessage> captor =
                ArgumentCaptor.forClass(ChatWebSocketController.ErrorMessage.class);
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getUsername()), eq("/queue/errors"), captor.capture());
        assertEquals("missing", captor.getValue().getError());
    }

    @Test
    void sendMessageHandlesGenericException() {
        LecturboxdUserPrincipal principal = principal();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        ChatMessageRequest request = new ChatMessageRequest();
        when(chatService.sendMessage(any(), any())).thenThrow(new RuntimeException("boom"));

        controller.sendMessage(request, auth);

        ArgumentCaptor<ChatWebSocketController.ErrorMessage> captor =
                ArgumentCaptor.forClass(ChatWebSocketController.ErrorMessage.class);
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getUsername()), eq("/queue/errors"), captor.capture());
        assertEquals("Failed to send message: boom", captor.getValue().getError());
    }

    @Test
    void errorMessageSetters() {
        ChatWebSocketController.ErrorMessage error = new ChatWebSocketController.ErrorMessage("x");
        assertEquals("x", error.getError());
        error.setError("y");
        assertEquals("y", error.getError());
    }

    private static LecturboxdUserPrincipal principal() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("me@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(true);
        return new LecturboxdUserPrincipal(user);
    }

    private static ChatMessageResponse message(String senderEmail, String receiverEmail) {
        ChatMessageResponse.UserSummary sender =
                new ChatMessageResponse.UserSummary(UUID.randomUUID(), "S", senderEmail);
        ChatMessageResponse.UserSummary receiver =
                new ChatMessageResponse.UserSummary(UUID.randomUUID(), "R", receiverEmail);
        return new ChatMessageResponse(1L, "hi", sender, receiver, null, false);
    }
}
