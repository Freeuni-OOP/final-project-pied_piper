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

/**
 * EN: Lecture catalog API — create, search, read, update, and delete lecture entries.
 * KA: ლექციების კატალოგის API — ლექციების შექმნა, ძებნა, წაკითხვა, განახლება და წაშლა.
 */
@RestController
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    /**
     * EN: POST /api/lectures — creates a new lecture entry.
     * KA: POST /api/lectures — ქმნის ახალ ლექციის ჩანაწერს.
     */
    @PostMapping("/api/lectures")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureResponse create(@Valid @RequestBody LectureRequest request) {
        return lectureService.create(request);
    }

    /**
     * EN: GET /api/lectures/search?q= — paginated lecture search by query string.
     * KA: GET /api/lectures/search?q= — ლექციების გვერდებად დაყოფილი ძებნა მოთხოვნის სტრიქონით.
     */
    @GetMapping("/api/lectures/search")
    public Page<LectureResponse> search(
            @RequestParam String q,
            @ParameterObject Pageable pageable
    ) {
        return lectureService.search(q, pageable);
    }

    /**
     * EN: GET /api/lectures/{id} — returns a single lecture by ID.
     * KA: GET /api/lectures/{id} — აბრუნებს ერთ ლექციას ID-ით.
     */
    @GetMapping("/api/lectures/{id}")
    public LectureResponse getById(@PathVariable Long id) {
        return lectureService.getById(id);
    }

    /**
     * EN: GET /api/lectures — returns all lectures as a list.
     * KA: GET /api/lectures — აბრუნებს ყველა ლექციას სიად.
     */
    @GetMapping("/api/lectures")
    public List<LectureResponse> getAll() {
        return lectureService.getAll();
    }

    /**
     * EN: PUT /api/lectures/{id} — updates an existing lecture by ID.
     * KA: PUT /api/lectures/{id} — განაახლებს არსებულ ლექციას ID-ით.
     */
    @PutMapping("/api/lectures/{id}")
    public LectureResponse update(@PathVariable Long id, @Valid @RequestBody LectureRequest request) {
        return lectureService.update(id, request);
    }

    /**
     * EN: DELETE /api/lectures/{id} — deletes a lecture by ID.
     * KA: DELETE /api/lectures/{id} — შლის ლექციას ID-ით.
     */
    @DeleteMapping("/api/lectures/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        lectureService.delete(id);
    }
}
