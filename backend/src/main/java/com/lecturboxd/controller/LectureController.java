package com.lecturboxd.controller;

import com.lecturboxd.dto.request.LectureRequest;
import com.lecturboxd.dto.response.LectureResponse;
import com.lecturboxd.service.LectureService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/api/lectures")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureResponse create(@Valid @RequestBody LectureRequest request) {
        return lectureService.create(request);
    }

    @GetMapping("/api/lectures/search")
    public Page<LectureResponse> search(
            @RequestParam String q,
            @ParameterObject Pageable pageable
    ) {
        return lectureService.search(q, pageable);
    }

    @GetMapping("/api/lectures/{id}")
    public LectureResponse getById(@PathVariable Long id) {
        return lectureService.getById(id);
    }

    @GetMapping("/api/lectures")
    public List<LectureResponse> getAll() {
        return lectureService.getAll();
    }

    @PutMapping("/api/lectures/{id}")
    public LectureResponse update(@PathVariable Long id, @Valid @RequestBody LectureRequest request) {
        return lectureService.update(id, request);
    }

    @DeleteMapping("/api/lectures/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        lectureService.delete(id);
    }
}
