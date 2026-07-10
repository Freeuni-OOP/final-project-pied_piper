package com.lecturboxd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EN: Response DTO for a lecture review including author and lecture summaries.
 * KA: პასუხის DTO ლექციის მიმოხილვისთვის ავტორისა და ლექციის შეჯამებით.
 */
public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private AuthorSummary author;
    private LectureSummary lecture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponse() {
    }

    public ReviewResponse(
            Long id,
            Integer rating,
            String comment,
            AuthorSummary author,
            LectureSummary lecture,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.author = author;
        this.lecture = lecture;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AuthorSummary getAuthor() {
        return author;
    }

    public void setAuthor(AuthorSummary author) {
        this.author = author;
    }

    public LectureSummary getLecture() {
        return lecture;
    }

    public void setLecture(LectureSummary lecture) {
        this.lecture = lecture;
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
     * EN: Nested summary of the review author.
     * KA: მიმოხილვის ავტორის ჩადგმული შეჯამება.
     */
    public static class AuthorSummary {

        private UUID id;
        private String name;

        public AuthorSummary() {
        }

        public AuthorSummary(UUID id, String name) {
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

    /**
     * EN: Nested summary of the reviewed lecture.
     * KA: განხილული ლექციის ჩადგმული შეჯამება.
     */
    public static class LectureSummary {

        private Long id;
        private String title;

        public LectureSummary() {
        }

        public LectureSummary(Long id, String title) {
            this.id = id;
            this.title = title;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
