package com.lecturboxd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Response DTO representing a single chat message with sender and receiver summaries.
 * KA: პასუხის DTO, რომელიც ერთ ჩატის შეტყობინებას წარმოადგენს გამგზავნისა და მიმღების შეჯამებით.
 */
public class ChatMessageResponse {

    private Long id;
    private String content;
    private UserSummary sender;
    private UserSummary receiver;
    private LocalDateTime sentAt;
    private boolean read;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(Long id, String content, UserSummary sender, UserSummary receiver,
                               LocalDateTime sentAt, boolean read) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sentAt = sentAt;
        this.read = read;
    }

    /**
     * EN: Minimal user info embedded in a chat message response.
     * KA: მინიმალური მომხმარებლის ინფორმაცია ჩატის შეტყობინების პასუხში.
     */
    // Nested UserSummary class
    public static class UserSummary {
        private UUID id;
        private String name;
        private String email;

        public UserSummary() {
        }

        public UserSummary(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UserSummary(UUID id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserSummary getSender() {
        return sender;
    }

    public void setSender(UserSummary sender) {
        this.sender = sender;
    }

    public UserSummary getReceiver() {
        return receiver;
    }

    public void setReceiver(UserSummary receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
