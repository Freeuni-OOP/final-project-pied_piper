package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * EN: Request body for starting (or opening) a chat conversation with another user.
 * KA: მოთხოვნის სხეული სხვა მომხმარებელთან ჩატის საუბრის დასაწყებად (ან გასახსნელად).
 */
public class StartConversationRequest {

    @NotNull(message = "Receiver user id is required")
    private UUID receiverId;

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }
}
