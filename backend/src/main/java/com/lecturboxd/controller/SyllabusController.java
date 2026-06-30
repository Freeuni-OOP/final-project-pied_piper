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

@RestController
@RequestMapping("/api/syllabus")
public class SyllabusController {

    private final SyllabusService syllabusService;

    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @GetMapping("/faculties")
    public List<FacultyResponse> listFaculties() {
        return syllabusService.listFaculties();
    }

    @GetMapping("/faculties/{facultyId}/semesters")
    public List<SemesterResponse> listSemesters(@PathVariable Long facultyId) {
        return syllabusService.listSemesters(facultyId);
    }

    @GetMapping("/semesters/{semesterId}/subjects")
    public List<SubjectSummaryResponse> listSubjects(@PathVariable Long semesterId) {
        return syllabusService.listSubjects(semesterId);
    }

    @GetMapping("/subjects/{subjectId}")
    public SubjectSyllabusResponse getSubject(@PathVariable Long subjectId) {
        return syllabusService.getSubjectSyllabus(subjectId);
    }
}
