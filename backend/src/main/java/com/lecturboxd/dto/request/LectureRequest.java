package com.lecturboxd.dto.request;

import com.lecturboxd.entity.LectureType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * EN: Request body for creating or updating a lecture session under a subject.
 * KA: მოთხოვნის სხეული საგნის ქვეშ ლექციის სესიის შესაქმნელად ან განსაახლებლად.
 */
public class LectureRequest {

    @NotNull
    private Long subjectId;

    @NotNull
    @Min(1)
    @Max(30)
    private Integer week;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer lectureNumber;

    @NotNull
    private LectureType type;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 10000)
    private String description;

    @Size(max = 10000)
    private String reading;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getLectureNumber() {
        return lectureNumber;
    }

    public void setLectureNumber(Integer lectureNumber) {
        this.lectureNumber = lectureNumber;
    }

    public LectureType getType() {
        return type;
    }

    public void setType(LectureType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }
}
