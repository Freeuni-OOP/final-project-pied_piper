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
import jakarta.persistence.UniqueConstraint;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Direct-message thread between exactly two users (unique pair constraint).
 * KA: პირდაპირი შეტყობინებების თრედი ზუსტად ორ მომხმარებელს შორის (უნიკალური წყვილის შეზღუდვა).
 */
@Entity
@Table(
        name = "conversations",
        uniqueConstraints = @UniqueConstraint(name = "unique_conversation", columnNames = {"user1_id", "user2_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EN: FK to the first participant (part of unique pair) | KA: FK პირველ მონაწილეზე (უნიკალური წყვილის ნაწილი)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    // EN: FK to the second participant (part of unique pair) | KA: FK მეორე მონაწილეზე (უნიკალური წყვილის ნაწილი)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // EN: Last update time (used to sort conversations by recency) | KA: ბოლო განახლების დრო (საუბრების სიახლის მიხედვით დალაგებისთვის)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * EN: Returns the other participant given one user's UUID, or null if the id is not in this conversation.
     * KA: აბრუნებს მეორე მონაწილეს ერთი მომხმარებლის UUID-ით, ან null-ს თუ id ამ საუბარში არ არის.
     */
    public User getOtherUser(UUID userId) {
        if (user1.getId().equals(userId)) {
            return user2;
        } else if (user2.getId().equals(userId)) {
            return user1;
        }
        return null;
    }
}
