package com.lecturboxd.dto.response;

import java.time.LocalDateTime;

public class FacultyResponse {

    private Long id;
    private String name;
    private long semesterCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FacultyResponse() {
    }

    public FacultyResponse(
            Long id,
            String name,
            long semesterCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.semesterCount = semesterCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSemesterCount() {
        return semesterCount;
    }

    public void setSemesterCount(long semesterCount) {
        this.semesterCount = semesterCount;
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
}
