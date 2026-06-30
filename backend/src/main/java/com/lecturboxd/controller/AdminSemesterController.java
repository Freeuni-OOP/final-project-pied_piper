package com.lecturboxd.controller;

import com.lecturboxd.dto.request.SemesterCreateRequest;
import com.lecturboxd.dto.request.SemesterUpdateRequest;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.service.SemesterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminSemesterController {

    private final SemesterService semesterService;

    public AdminSemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @PostMapping("/faculties/{facultyId}/semesters")
    @ResponseStatus(HttpStatus.CREATED)
    public SemesterResponse create(
            @PathVariable Long facultyId,
            @Valid @RequestBody SemesterCreateRequest request
    ) {
        return semesterService.create(facultyId, request);
    }

    @GetMapping("/faculties/{facultyId}/semesters")
    public List<SemesterResponse> findByFaculty(@PathVariable Long facultyId) {
        return semesterService.findByFacultyId(facultyId);
    }

    @PutMapping("/semesters/{id}")
    public SemesterResponse update(@PathVariable Long id, @Valid @RequestBody SemesterUpdateRequest request) {
        return semesterService.update(id, request);
    }

    @DeleteMapping("/semesters/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        semesterService.delete(id);
    }
}
