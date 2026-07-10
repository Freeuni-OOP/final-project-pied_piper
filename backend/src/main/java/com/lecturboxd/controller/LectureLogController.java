package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.service.LectureLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LectureLogController {

    private final LectureLogService lectureLogService;

    public LectureLogController(LectureLogService lectureLogService) {
        this.lectureLogService = lectureLogService;
    }

    @PostMapping("/api/lectures/{lectureId}/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureLogResponse createLog(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long lectureId,
            @Valid @RequestBody(required = false) LectureLogRequest request
    ) {
        LectureLogRequest body = request != null ? request : new LectureLogRequest();
        return lectureLogService.createLog(principal.getId(), lectureId, body);
    }

    @GetMapping("/api/lectures/{lectureId}/logs/me")
    public LectureLogResponse getMyLog(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long lectureId
    ) {
        return lectureLogService.getMyLog(principal.getId(), lectureId);
    }

    @DeleteMapping("/api/lectures/{lectureId}/logs/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyLog(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long lectureId
    ) {
        lectureLogService.deleteMyLog(principal.getId(), lectureId);
    }

    @GetMapping("/api/users/{userId}/logs")
    public Page<LectureLogResponse> getLogsByUser(@PathVariable UUID userId, Pageable pageable) {
        return lectureLogService.getLogsByUser(userId, pageable);
    }

    @DeleteMapping("/api/logs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLog(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            @PathVariable Long id
    ) {
        lectureLogService.deleteLog(principal.getId(), id);
    }
}
