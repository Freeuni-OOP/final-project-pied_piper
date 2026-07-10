package com.lecturboxd.websocket;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ChatMessageRequest;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * WebSocket controller for real-time chat.
 * convertAndSendToUser must use the Principal name (email), not the user UUID.
 */
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof LecturboxdUserPrincipal)) {
                sendError(null, "Authentication required");
                return;
            }

            LecturboxdUserPrincipal principal = (LecturboxdUserPrincipal) authentication.getPrincipal();
            UUID senderId = principal.getId();

            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);

            // Principal name is email — must match for user destinations
            String receiverEmail = messageResponse.getReceiver().getEmail();
            String senderEmail = messageResponse.getSender().getEmail();

            if (receiverEmail != null) {
                messagingTemplate.convertAndSendToUser(receiverEmail, "/queue/messages", messageResponse);
            }
            if (senderEmail != null) {
                messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", messageResponse);
            }

        } catch (ResourceNotFoundException ex) {
            sendError(authentication, ex.getMessage());
        } catch (Exception ex) {
            sendError(authentication, "Failed to send message: " + ex.getMessage());
        }
    }

    private void sendError(Authentication authentication, String errorMessage) {
        if (authentication != null && authentication.getPrincipal() instanceof LecturboxdUserPrincipal) {
            LecturboxdUserPrincipal principal = (LecturboxdUserPrincipal) authentication.getPrincipal();
            messagingTemplate.convertAndSendToUser(
                    principal.getUsername(),
                    "/queue/errors",
                    new ErrorMessage(errorMessage)
            );
        }
    }

    public static class ErrorMessage {
        private String error;

        public ErrorMessage(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
