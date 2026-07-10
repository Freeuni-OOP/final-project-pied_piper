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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EN: User diary entry that a lecture was watched/attended (one log per user+lecture).
 * KA: მომხმარებლის დღიურის ჩანაწერი, რომ ლექცია ნახა/დაესწრო (ერთი ლოგი user+lecture წყვილზე).
 */
@Entity
@Table(name = "lecture_logs", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lecture_id"}))
@EntityListeners(AuditingEntityListener.class)
public class LectureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EN: FK to the user who logged the lecture | KA: FK მომხმარებელზე, რომელმაც ლექცია დაალოგა
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // EN: FK to the logged lecture (unique with user_id) | KA: FK დალოგებულ ლექციაზე (უნიკალური user_id-თან ერთად)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // EN: Calendar date the user marked as watched | KA: კალენდარული თარიღი, როცა მომხმარებელმა ნახვა მონიშნა
    @Column(name = "watched_at", nullable = false)
    private LocalDate watchedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public LocalDate getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDate watchedAt) {
        this.watchedAt = watchedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
