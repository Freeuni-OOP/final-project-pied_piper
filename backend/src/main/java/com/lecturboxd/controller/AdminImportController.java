package com.lecturboxd.controller;

import com.lecturboxd.dto.request.FacultyImportRequest;
import com.lecturboxd.dto.response.ImportSummaryResponse;
import com.lecturboxd.service.AdminImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/import")
public class AdminImportController {

    private final AdminImportService adminImportService;

    public AdminImportController(AdminImportService adminImportService) {
        this.adminImportService = adminImportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ImportSummaryResponse importFacultyData(@Valid @RequestBody FacultyImportRequest request) {
        return adminImportService.importFacultyData(request);
    }
}
