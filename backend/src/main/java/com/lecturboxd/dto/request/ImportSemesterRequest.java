package com.lecturboxd.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportSemesterRequest {

    @NotBlank(message = "Semester number is required")
    private String number;

    @Valid
    private List<ImportSubjectRequest> subjects = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<ImportSubjectRequest> getSubjects() {
        return subjects == null ? Collections.emptyList() : subjects;
    }

    public void setSubjects(List<ImportSubjectRequest> subjects) {
        this.subjects = subjects == null ? new ArrayList<>() : subjects;
    }
}
