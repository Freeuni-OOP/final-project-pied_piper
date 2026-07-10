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
 * WebSocket controller for handling real-time chat messages via STOMP.
 *
 * Clients should:
 * 1. Connect to /ws with Authorization: Bearer <JWT_TOKEN> header
 * 2. Send messages to /app/chat.send with { receiverId, content }
 * 3. Subscribe to /user/queue/messages to receive incoming messages
 * 4. Subscribe to /user/queue/errors to receive error messages
 */
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle incoming chat messages. Endpoint: /app/chat.send
     * Expected payload: { "receiverId": "UUID", "content": "message text" }
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof LecturboxdUserPrincipal)) {
                sendError(null, "Authentication required");
                return;
            }

            LecturboxdUserPrincipal principal = (LecturboxdUserPrincipal) authentication.getPrincipal();
            UUID senderId = principal.getId();

            // Save message to database
            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);

            // Send to receiver via point-to-point messaging
            messagingTemplate.convertAndSendToUser(
                    messageResponse.getReceiver().getId().toString(),
                    "/queue/messages",
                    messageResponse
            );

            // Also send confirmation back to sender
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/messages",
                    messageResponse
            );

        } catch (ResourceNotFoundException ex) {
            sendError(authentication, ex.getMessage());
        } catch (Exception ex) {
            sendError(authentication, "Failed to send message: " + ex.getMessage());
        }
    }

    /**
     * Send an error message to a specific user
     */
    private void sendError(Authentication authentication, String errorMessage) {
        if (authentication != null && authentication.getPrincipal() instanceof LecturboxdUserPrincipal) {
            LecturboxdUserPrincipal principal = (LecturboxdUserPrincipal) authentication.getPrincipal();
            messagingTemplate.convertAndSendToUser(
                    principal.getId().toString(),
                    "/queue/errors",
                    new ErrorMessage(errorMessage)
            );
        }
    }

    /**
     * Simple error message wrapper
     */
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
