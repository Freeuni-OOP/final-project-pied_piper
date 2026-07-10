package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * EN: Request body for sending a private chat message to another user.
 * KA: მოთხოვნის სხეული პირადი ჩატის შეტყობინების სხვა მომხმარებელთან გასაგზავნად.
 */
public class ChatMessageRequest {

    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;

    @NotBlank(message = "Message content cannot be blank")
    private String content;

    public ChatMessageRequest() {
    }

    public ChatMessageRequest(UUID receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
