package com.lecturboxd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationResponse {

    private Long id;
    private UserSummary otherUser;
    private LocalDateTime updatedAt;
    private Long unreadCount;

    public ConversationResponse() {
    }

    public ConversationResponse(Long id, UserSummary otherUser, LocalDateTime updatedAt, Long unreadCount) {
        this.id = id;
        this.otherUser = otherUser;
        this.updatedAt = updatedAt;
        this.unreadCount = unreadCount;
    }

    // Nested UserSummary class
    public static class UserSummary {
        private UUID id;
        private String name;

        public UserSummary() {
        }

        public UserSummary(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSummary getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(UserSummary otherUser) {
        this.otherUser = otherUser;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
