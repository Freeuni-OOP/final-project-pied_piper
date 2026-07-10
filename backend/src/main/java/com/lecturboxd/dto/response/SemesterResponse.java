package com.lecturboxd.dto.response;

import java.time.LocalDateTime;

/**
 * EN: Response DTO for a semester, including faculty id and subject count.
 * KA: პასუხის DTO სემესტრისთვის, ფაკულტეტის id-ისა და საგნების რაოდენობის ჩათვლით.
 */
public class SemesterResponse {

    private Long id;
    private String number;
    private Long facultyId;
    private long subjectCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SemesterResponse() {
    }

    public SemesterResponse(
            Long id,
            String number,
            Long facultyId,
            long subjectCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.number = number;
        this.facultyId = facultyId;
        this.subjectCount = subjectCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public long getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(long subjectCount) {
        this.subjectCount = subjectCount;
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
