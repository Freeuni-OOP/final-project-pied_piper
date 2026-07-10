package com.lecturboxd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Single chat message belonging to a conversation between two users.
 * KA: ერთი ჩატის შეტყობინება, რომელიც ეკუთვნის ორ მომხმარებელს შორის საუბარს.
 */
@Entity
@Table(name = "chat_messages")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EN: FK to the parent conversation | KA: FK მშობელ საუბარზე
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    // EN: FK to the user who sent the message | KA: FK მომხმარებელზე, რომელმაც შეტყობინება გააგზავნა
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // EN: FK to the user who receives the message | KA: FK მომხმარებელზე, რომელიც შეტყობინებას იღებს
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // EN: Message body text | KA: შეტყობინების ტექსტი
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // EN: When the message was sent (business timestamp) | KA: როდის გაიგზავნა შეტყობინება (ბიზნეს დროის ნიშნული)
    @Column(nullable = false)
    private LocalDateTime sentAt;

    // EN: Whether the receiver has read the message | KA: წაიკითხა თუ არა მიმღებმა შეტყობინება
    @Column(nullable = false)
    private boolean read = false;

    // EN: Audit timestamp when the row was persisted | KA: აუდიტის დროის ნიშნული ჩანაწერის შენახვისას
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
