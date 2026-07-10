package com.lecturboxd.dto.response;

import com.lecturboxd.entity.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Response DTO for a single activity-feed item (review or lecture-log activity).
 * KA: პასუხის DTO აქტივობის ფიდის ერთი ელემენტისთვის (მიმოხილვა ან ლექციის ლოგის აქტივობა).
 */
public class FeedItemResponse {

    private Long id;
    private ActivityType type;
    /** EN: User who performed the activity. KA: მომხმარებელი, რომელმაც აქტივობა შეასრულა. */
    private UUID actorId;
    private String actorName;
    private Long lectureId;
    private String lectureTitle;
    private Long reviewId;
    /** EN: Present when the activity is a lecture-log event. KA: არსებობს, როცა აქტივობა ლექციის ლოგის მოვლენაა. */
    private Long lectureLogId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public FeedItemResponse() {
    }

    public FeedItemResponse(
            Long id,
            ActivityType type,
            UUID actorId,
            String actorName,
            Long lectureId,
            String lectureTitle,
            Long reviewId,
            Long lectureLogId,
            Integer rating,
            String comment,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.type = type;
        this.actorId = actorId;
        this.actorName = actorName;
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.reviewId = reviewId;
        this.lectureLogId = lectureLogId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Long getLectureId() {
        return lectureId;
    }

    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getLectureLogId() {
        return lectureLogId;
    }

    public void setLectureLogId(Long lectureLogId) {
        this.lectureLogId = lectureLogId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
