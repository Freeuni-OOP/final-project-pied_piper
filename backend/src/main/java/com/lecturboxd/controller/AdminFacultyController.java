package com.lecturboxd.controller;

import com.lecturboxd.dto.request.FacultyCreateRequest;
import com.lecturboxd.dto.request.FacultyUpdateRequest;
import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.service.FacultyService;
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
 * EN: Admin faculty API — CRUD for faculties (admin API key required).
 * KA: ადმინის ფაკულტეტების API — ფაკულტეტების CRUD (საჭიროა ადმინის API გასაღები).
 */
@RestController
@RequestMapping("/api/admin/faculties")
public class AdminFacultyController {

    private final FacultyService facultyService;

    public AdminFacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    /**
     * EN: POST /api/admin/faculties — creates a faculty (admin API key).
     * KA: POST /api/admin/faculties — ქმნის ფაკულტეტს (ადმინის API გასაღები).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyResponse create(@Valid @RequestBody FacultyCreateRequest request) {
        return facultyService.create(request);
    }

    /**
     * EN: GET /api/admin/faculties — lists all faculties (admin API key).
     * KA: GET /api/admin/faculties — აბრუნებს ყველა ფაკულტეტს (ადმინის API გასაღები).
     */
    @GetMapping
    public List<FacultyResponse> findAll() {
        return facultyService.findAll();
    }

    /**
     * EN: GET /api/admin/faculties/{id} — returns a faculty by ID (admin API key).
     * KA: GET /api/admin/faculties/{id} — აბრუნებს ფაკულტეტს ID-ით (ადმინის API გასაღები).
     */
    @GetMapping("/{id}")
    public FacultyResponse findById(@PathVariable Long id) {
        return facultyService.findById(id);
    }

    /**
     * EN: PUT /api/admin/faculties/{id} — updates a faculty by ID (admin API key).
     * KA: PUT /api/admin/faculties/{id} — განაახლებს ფაკულტეტს ID-ით (ადმინის API გასაღები).
     */
    @PutMapping("/{id}")
    public FacultyResponse update(@PathVariable Long id, @Valid @RequestBody FacultyUpdateRequest request) {
        return facultyService.update(id, request);
    }

    /**
     * EN: DELETE /api/admin/faculties/{id} — deletes a faculty by ID (admin API key).
     * KA: DELETE /api/admin/faculties/{id} — შლის ფაკულტეტს ID-ით (ადმინის API გასაღები).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facultyService.delete(id);
    }
}
