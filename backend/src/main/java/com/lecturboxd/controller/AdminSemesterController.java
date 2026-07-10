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

/**
 * EN: Admin semester API — CRUD for semesters under faculties (admin API key required).
 * KA: ადმინის სემესტრების API — სემესტრების CRUD ფაკულტეტების ქვეშ (საჭიროა ადმინის API გასაღები).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminSemesterController {

    private final SemesterService semesterService;

    public AdminSemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    /**
     * EN: POST /api/admin/faculties/{facultyId}/semesters — creates a semester for a faculty (admin API key).
     * KA: POST /api/admin/faculties/{facultyId}/semesters — ქმნის სემესტრს ფაკულტეტისთვის (ადმინის API გასაღები).
     */
    @PostMapping("/faculties/{facultyId}/semesters")
    @ResponseStatus(HttpStatus.CREATED)
    public SemesterResponse create(
            @PathVariable Long facultyId,
            @Valid @RequestBody SemesterCreateRequest request
    ) {
        return semesterService.create(facultyId, request);
    }

    /**
     * EN: GET /api/admin/faculties/{facultyId}/semesters — lists semesters for a faculty (admin API key).
     * KA: GET /api/admin/faculties/{facultyId}/semesters — აბრუნებს ფაკულტეტის სემესტრებს (ადმინის API გასაღები).
     */
    @GetMapping("/faculties/{facultyId}/semesters")
    public List<SemesterResponse> findByFaculty(@PathVariable Long facultyId) {
        return semesterService.findByFacultyId(facultyId);
    }

    /**
     * EN: PUT /api/admin/semesters/{id} — updates a semester by ID (admin API key).
     * KA: PUT /api/admin/semesters/{id} — განაახლებს სემესტრს ID-ით (ადმინის API გასაღები).
     */
    @PutMapping("/semesters/{id}")
    public SemesterResponse update(@PathVariable Long id, @Valid @RequestBody SemesterUpdateRequest request) {
        return semesterService.update(id, request);
    }

    /**
     * EN: DELETE /api/admin/semesters/{id} — deletes a semester by ID (admin API key).
     * KA: DELETE /api/admin/semesters/{id} — შლის სემესტრს ID-ით (ადმინის API გასაღები).
     */
    @DeleteMapping("/semesters/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        semesterService.delete(id);
    }
}
