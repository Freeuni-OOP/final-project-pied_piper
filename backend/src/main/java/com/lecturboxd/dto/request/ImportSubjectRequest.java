package com.lecturboxd.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportSubjectRequest {

    @NotBlank(message = "Subject name is required")
    private String name;

    @NotBlank(message = "Lecturer is required")
    private String lecturer;

    @NotBlank(message = "Subject type is required")
    private String type;

    private String description;

    @Valid
    private List<ImportLectureRequest> lectures = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImportLectureRequest> getLectures() {
        return lectures == null ? Collections.emptyList() : lectures;
    }

    public void setLectures(List<ImportLectureRequest> lectures) {
        this.lectures = lectures == null ? new ArrayList<>() : lectures;
    }
}
