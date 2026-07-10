package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.response.ChatMessageResponse;
import com.lecturboxd.dto.response.ConversationResponse;
import com.lecturboxd.service.ChatService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Get paginated chat history for a conversation
     */
    @GetMapping("/{conversationId}")
    public Page<ChatMessageResponse> getChatHistory(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long conversationId,
            @ParameterObject Pageable pageable
    ) {
        return chatService.getChatHistory(principal.getId(), conversationId, pageable);
    }

    /**
     * Get all conversations for the current user
     */
    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal
    ) {
        return chatService.getUserConversations(principal.getId());
    }

    /**
     * Mark a message as read
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
