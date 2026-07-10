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

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal
    ) {
        return chatService.getUserConversations(principal.getId());
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse startConversation(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody StartConversationRequest request
    ) {
        return chatService.startConversation(principal.getId(), request.getReceiverId());
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse sendMessage(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse messageResponse = chatService.sendMessage(principal.getId(), request);

        // Push live updates when possible (Principal name = email)
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

    @GetMapping("/conversations/{conversationId}/messages")
    public Page<ChatMessageResponse> getChatHistory(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId,
            @ParameterObject Pageable pageable
    ) {
        return chatService.getChatHistory(principal.getId(), conversationId, pageable);
    }

    @GetMapping("/{conversationId}")
    public Page<ChatMessageResponse> getChatHistoryLegacy(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId,
            @ParameterObject Pageable pageable
    ) {
        return chatService.getChatHistory(principal.getId(), conversationId, pageable);
    }

    @PutMapping("/conversations/{conversationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markConversationAsRead(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId
    ) {
        chatService.markMessagesAsRead(principal.getId(), conversationId);
    }

    @PutMapping("/{messageId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markMessageAsRead(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long messageId
    ) {
        chatService.markMessageAsRead(principal.getId(), messageId);
    }
}
