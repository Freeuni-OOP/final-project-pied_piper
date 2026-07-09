package com.lecturboxd.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LectureLogResponse {

    private Long id;
    private UUID userId;
    private Long lectureId;
    private String lectureTitle;
    private LocalDate watchedAt;
    private LocalDateTime createdAt;

    public LectureLogResponse() {
    }

    public LectureLogResponse(
            Long id,
            UUID userId,
            Long lectureId,
            String lectureTitle,
            LocalDate watchedAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.watchedAt = watchedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
