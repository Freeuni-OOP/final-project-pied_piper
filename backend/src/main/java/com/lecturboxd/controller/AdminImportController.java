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

/**
 * EN: Admin bulk-import API — imports faculty/syllabus data in one request (admin API key required).
 * KA: ადმინის მასობრივი იმპორტის API — ფაკულტეტის/სილაბუსის მონაცემების იმპორტი ერთი მოთხოვნით (საჭიროა ადმინის API გასაღები).
 */
@RestController
@RequestMapping("/api/admin/import")
public class AdminImportController {

    private final AdminImportService adminImportService;

    public AdminImportController(AdminImportService adminImportService) {
        this.adminImportService = adminImportService;
    }

    /**
     * EN: POST /api/admin/import — imports nested faculty data and returns an import summary (admin API key).
     * KA: POST /api/admin/import — იმპორტებს ჩადგმულ ფაკულტეტის მონაცემებს და აბრუნებს იმპორტის შეჯამებას (ადმინის API გასაღები).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ImportSummaryResponse importFacultyData(@Valid @RequestBody FacultyImportRequest request) {
        return adminImportService.importFacultyData(request);
    }
}
