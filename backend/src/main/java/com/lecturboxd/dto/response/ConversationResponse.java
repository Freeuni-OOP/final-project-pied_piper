package com.lecturboxd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Response DTO for a chat conversation as seen by the current user.
 * KA: პასუხის DTO ჩატის საუბრისთვის მიმდინარე მომხმარებლის თვალსაზრისით.
 */
public class ConversationResponse {

    private Long id;
    /** EN: The other participant relative to the current user. KA: მეორე მონაწილე მიმდინარე მომხმარებელთან მიმართებით. */
    private UserSummary otherUser;
    private LocalDateTime updatedAt;
    /** EN: Number of unread messages in this conversation for the current user. KA: ამ საუბრში წაუკითხავი შეტყობინებების რაოდენობა მიმდინარე მომხმარებლისთვის. */
    private Long unreadCount;

    public ConversationResponse() {
    }

    public ConversationResponse(Long id, UserSummary otherUser, LocalDateTime updatedAt, Long unreadCount) {
        this.id = id;
        this.otherUser = otherUser;
        this.updatedAt = updatedAt;
        this.unreadCount = unreadCount;
    }

    /**
     * EN: Minimal summary of the other conversation participant.
     * KA: საუბრის მეორე მონაწილის მინიმალური შეჯამება.
     */
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
