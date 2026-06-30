package com.lecturboxd.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FacultyImportRequest {

    @NotBlank(message = "Faculty name is required")
    private String faculty;

    @Valid
    private List<ImportSemesterRequest> semesters = new ArrayList<>();

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public List<ImportSemesterRequest> getSemesters() {
        return semesters == null ? Collections.emptyList() : semesters;
    }

    public void setSemesters(List<ImportSemesterRequest> semesters) {
        this.semesters = semesters == null ? new ArrayList<>() : semesters;
    }
}
