package com.lecturboxd.controller;

import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.dto.response.SubjectSummaryResponse;
import com.lecturboxd.dto.response.SubjectSyllabusResponse;
import com.lecturboxd.service.SyllabusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * EN: Public syllabus browse API — faculties, semesters, subjects, and subject detail.
 * KA: საჯარო სილაბუსის დათვალიერების API — ფაკულტეტები, სემესტრები, საგნები და საგნის დეტალი.
 */
@RestController
@RequestMapping("/api/syllabus")
public class SyllabusController {

    private final SyllabusService syllabusService;

    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    /**
     * EN: GET /api/syllabus/faculties — lists all faculties.
     * KA: GET /api/syllabus/faculties — აბრუნებს ყველა ფაკულტეტს.
     */
    @GetMapping("/faculties")
    public List<FacultyResponse> listFaculties() {
        return syllabusService.listFaculties();
    }

    /**
     * EN: GET /api/syllabus/faculties/{facultyId}/semesters — lists semesters for a faculty.
     * KA: GET /api/syllabus/faculties/{facultyId}/semesters — აბრუნებს ფაკულტეტის სემესტრებს.
     */
    @GetMapping("/faculties/{facultyId}/semesters")
    public List<SemesterResponse> listSemesters(@PathVariable Long facultyId) {
        return syllabusService.listSemesters(facultyId);
    }

    /**
     * EN: GET /api/syllabus/semesters/{semesterId}/subjects — lists subject summaries for a semester.
     * KA: GET /api/syllabus/semesters/{semesterId}/subjects — აბრუნებს სემესტრის საგნების შეჯამებებს.
     */
    @GetMapping("/semesters/{semesterId}/subjects")
    public List<SubjectSummaryResponse> listSubjects(@PathVariable Long semesterId) {
        return syllabusService.listSubjects(semesterId);
    }

    /**
     * EN: GET /api/syllabus/subjects/{subjectId} — returns full syllabus detail for a subject.
     * KA: GET /api/syllabus/subjects/{subjectId} — აბრუნებს საგნის სრულ სილაბუსის დეტალს.
     */
    @GetMapping("/subjects/{subjectId}")
    public SubjectSyllabusResponse getSubject(@PathVariable Long subjectId) {
        return syllabusService.getSubjectSyllabus(subjectId);
    }
}
