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

@RestController
@RequestMapping("/api/admin/faculties")
public class AdminFacultyController {

    private final FacultyService facultyService;

    public AdminFacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyResponse create(@Valid @RequestBody FacultyCreateRequest request) {
        return facultyService.create(request);
    }

    @GetMapping
    public List<FacultyResponse> findAll() {
        return facultyService.findAll();
    }

    @GetMapping("/{id}")
    public FacultyResponse findById(@PathVariable Long id) {
        return facultyService.findById(id);
    }

    @PutMapping("/{id}")
    public FacultyResponse update(@PathVariable Long id, @Valid @RequestBody FacultyUpdateRequest request) {
        return facultyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facultyService.delete(id);
    }
}
