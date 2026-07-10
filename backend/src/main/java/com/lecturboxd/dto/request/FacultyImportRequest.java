package com.lecturboxd.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EN: Root payload for bulk-importing a faculty tree (semesters, subjects, lectures).
 * KA: ძირითადი პეილოდი ფაკულტეტის ხის მასობრივი იმპორტისთვის (სემესტრები, საგნები, ლექციები).
 */
public class FacultyImportRequest {

    /** EN: Faculty display name used as the import root key. KA: ფაკულტეტის საჩვენებელი სახელი იმპორტის ძირითად გასაღებად. */
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
