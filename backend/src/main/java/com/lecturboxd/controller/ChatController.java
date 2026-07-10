package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.ChatMessageRequest;
import com.lecturboxd.dto.request.StartConversationRequest;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.service.ChatService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * EN: Direct messaging API — conversations, send/history, read receipts; also pushes live updates over WebSocket.
 * KA: პირადი შეტყობინებების API — საუბრები, გაგზავნა/ისტორია, წაკითხვის დადასტურება; ასევე ცოცხალი განახლებები WebSocket-ით.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * EN: GET /api/chat/conversations — lists conversations for the current user (JWT required).
     * KA: GET /api/chat/conversations — აბრუნებს მიმდინარე მომხმარებლის საუბრებს (საჭიროა JWT).
     */
    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal
    ) {
        return chatService.getUserConversations(principal.getId());
    }

    /**
     * EN: POST /api/chat/conversations — starts (or returns) a conversation with a receiver (JWT required).
     * KA: POST /api/chat/conversations — იწყებს (ან აბრუნებს) საუბარს მიმღებთან (საჭიროა JWT).
     */
    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse startConversation(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody StartConversationRequest request
    ) {
        return chatService.startConversation(principal.getId(), request.getReceiverId());
    }

    /**
     * EN: POST /api/chat/send — sends a chat message and pushes it to both users via WebSocket (JWT required).
     * KA: POST /api/chat/send — აგზავნის შეტყობინებას და უბიძგებს ორივე მომხმარებელს WebSocket-ით (საჭიროა JWT).
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse sendMessage(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse messageResponse = chatService.sendMessage(principal.getId(), request);

        // EN: Push live updates when possible (Principal name = email) | KA: ცოცხალი განახლებების გაგზავნა შესაძლებლობისას (Principal-ის სახელი = ელფოსტა)
        String receiverEmail = messageResponse.getReceiver().getEmail();
        String senderEmail = messageResponse.getSender().getEmail();
        if (receiverEmail != null) {
            messagingTemplate.convertAndSendToUser(receiverEmail, "/queue/messages", messageResponse);
        }
        if (senderEmail != null) {
            messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", messageResponse);
        }

        return messageResponse;
    }

    /**
     * EN: GET /api/chat/conversations/{conversationId}/messages — paginated chat history (JWT required).
     * KA: GET /api/chat/conversations/{conversationId}/messages — ჩატის ისტორია გვერდებად (საჭიროა JWT).
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public Page<ChatMessageResponse> getChatHistory(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId,
            @ParameterObject Pageable pageable
    ) {
        return chatService.getChatHistory(principal.getId(), conversationId, pageable);
    }

    /**
     * EN: GET /api/chat/{conversationId} — legacy alias for chat history (JWT required).
     * KA: GET /api/chat/{conversationId} — ჩატის ისტორიის ძველი ალიასი (საჭიროა JWT).
     */
    @GetMapping("/{conversationId}")
    public Page<ChatMessageResponse> getChatHistoryLegacy(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId,
            @ParameterObject Pageable pageable
    ) {
        return chatService.getChatHistory(principal.getId(), conversationId, pageable);
    }

    /**
     * EN: PUT /api/chat/conversations/{conversationId}/read — marks all messages in a conversation as read (JWT required).
     * KA: PUT /api/chat/conversations/{conversationId}/read — მონიშნავს საუბრის ყველა შეტყობინებას წაკითხულად (საჭიროა JWT).
     */
    @PutMapping("/conversations/{conversationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markConversationAsRead(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId
    ) {
        chatService.markMessagesAsRead(principal.getId(), conversationId);
    }

    /**
     * EN: PUT /api/chat/{messageId}/read — marks a single message as read (JWT required).
     * KA: PUT /api/chat/{messageId}/read — მონიშნავს ერთ შეტყობინებას წაკითხულად (საჭიროა JWT).
     */
    @PutMapping("/{messageId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markMessageAsRead(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long messageId
    ) {
        chatService.markMessageAsRead(principal.getId(), messageId);
    }
}
