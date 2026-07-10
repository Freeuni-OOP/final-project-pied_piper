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
 * EN: WebSocket controller for real-time chat; convertAndSendToUser must use Principal name (email), not UUID.
 * KA: WebSocket კონტროლერი რეალურ დროში ჩატისთვის; convertAndSendToUser უნდა იყენებდეს Principal-ის სახელს (ელფოსტას), არა UUID-ს.
 */
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * EN: Injects chat persistence service and STOMP messaging template.
     * KA: ინჯექციას უკეთებს ჩატის შენახვის სერვისს და STOMP მესიჯინგის შაბლონს.
     */
    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * EN: Handles /app/chat.send: persists the message and pushes it to sender and receiver queues.
     * KA: ამუშავებს /app/chat.send-ს: ინახავს შეტყობინებას და უგზავნის გამგზავნისა და მიმღების რიგებში.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Authentication authentication) {
        try {
            // EN: Require an authenticated LecturboxdUserPrincipal before sending | KA: გაგზავნამდე საჭიროა ავთენტიფიცირებული LecturboxdUserPrincipal
            if (authentication == null || !(authentication.getPrincipal() instanceof LecturboxdUserPrincipal)) {
                sendError(null, "Authentication required");
                return;
            }

            LecturboxdUserPrincipal principal = (LecturboxdUserPrincipal) authentication.getPrincipal();
            UUID senderId = principal.getId();

            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);

            // EN: Principal name is email — must match for user destinations | KA: Principal-ის სახელი არის ელფოსტა — უნდა ემთხვეოდეს user დანიშნულებებს
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

    /**
     * EN: Sends an error payload to the authenticated user's /queue/errors destination when possible.
     * KA: შესაძლებლობისას უგზავნის შეცდომის payload-ს ავთენტიფიცირებული მომხმარებლის /queue/errors დანიშნულებაზე.
     */
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

    /**
     * EN: Simple DTO carrying a chat WebSocket error message string.
     * KA: მარტივი DTO, რომელიც ატარებს ჩატის WebSocket შეცდომის შეტყობინების სტრიქონს.
     */
    public static class ErrorMessage {
        private String error;

        /**
         * EN: Creates an error message with the given text.
         * KA: ქმნის შეცდომის შეტყობინებას მოცემული ტექსტით.
         */
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
