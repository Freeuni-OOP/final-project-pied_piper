package com.lecturboxd.dto.request;

import com.lecturboxd.entity.LectureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * EN: Nested import payload describing a single lecture/session within a subject.
 * KA: ჩადგმული იმპორტის პეილოდი, რომელიც საგანში ერთ ლექციას/სესიას აღწერს.
 */
public class ImportLectureRequest {

    @NotNull(message = "Week is required")
    private Integer week;

    @NotNull(message = "Lecture number is required")
    private Integer lectureNumber;

    @NotNull(message = "Lecture type is required")
    private LectureType type;

    @NotBlank(message = "Lecture title is required")
    private String title;

    private String description;

    /** EN: Optional assigned reading / materials for the session. KA: სესიის სურვილისამებრი საკითხავი / მასალები. */
    private String reading;

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
