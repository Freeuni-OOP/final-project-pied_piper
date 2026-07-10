package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

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
