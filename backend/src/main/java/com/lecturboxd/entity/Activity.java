package com.lecturboxd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * EN: Feed activity row — records that a user created a review or logged a lecture.
 * KA: ფიდის აქტივობის ჩანაწერი — აღრიცხავს, რომ მომხმარებელმა შექმნა მიმოხილვა ან დაალოგა ლექცია.
 */
@Entity
@Table(name = "activities")
@EntityListeners(AuditingEntityListener.class)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EN: FK to the user who performed the activity | KA: FK მომხმარებელზე, რომელმაც აქტივობა შეასრულა
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // EN: Activity kind (REVIEW_CREATED / LECTURE_LOGGED) | KA: აქტივობის ტიპი (REVIEW_CREATED / LECTURE_LOGGED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    // EN: FK to the lecture this activity relates to | KA: FK ლექციაზე, რომელსაც ეს აქტივობა ეხება
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // EN: Optional FK to the review (set when type is REVIEW_CREATED) | KA: არასავალდებულო FK მიმოხილვაზე (როცა ტიპი არის REVIEW_CREATED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    // EN: Optional lecture-log id reference (set when type is LECTURE_LOGGED) | KA: არასავალდებულო lecture-log id მითითება (როცა ტიპი არის LECTURE_LOGGED)
    @Column(name = "lecture_log_id")
    private Long lectureLogId;

    // EN: Audit timestamp when the activity was created | KA: აუდიტის დროის ნიშნული აქტივობის შექმნისას
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

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public Long getReviewId() {
        return review != null ? review.getId() : null;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Long getLectureLogId() {
        return lectureLogId;
    }

    public void setLectureLogId(Long lectureLogId) {
        this.lectureLogId = lectureLogId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
