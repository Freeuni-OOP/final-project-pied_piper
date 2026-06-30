package com.lecturboxd.service;

import com.lecturboxd.dto.request.FacultyImportRequest;
import com.lecturboxd.dto.request.ImportLectureRequest;
import com.lecturboxd.dto.request.ImportSemesterRequest;
import com.lecturboxd.dto.request.ImportSubjectRequest;
import com.lecturboxd.dto.response.ImportSummaryResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminImportService {

    private final FacultyRepository facultyRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final LectureRepository lectureRepository;

    public AdminImportService(
            FacultyRepository facultyRepository,
            SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            LectureRepository lectureRepository
    ) {
        this.facultyRepository = facultyRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public ImportSummaryResponse importFacultyData(FacultyImportRequest request) {
        ImportSummaryResponse summary = new ImportSummaryResponse();
        String facultyName = FacultyService.normalizeName(request.getFaculty());
        Faculty faculty = resolveOrCreateFaculty(facultyName, summary);

        for (ImportSemesterRequest semesterRequest : request.getSemesters()) {
            Semester semester = resolveOrCreateSemester(faculty, semesterRequest, summary);

            for (ImportSubjectRequest subjectRequest : semesterRequest.getSubjects()) {
                Subject subject = resolveOrCreateSubject(semester, subjectRequest, summary);

                for (ImportLectureRequest lectureRequest : subjectRequest.getLectures()) {
                    resolveOrCreateLecture(subject, lectureRequest, summary);
                }
            }
        }

        return summary;
    }

    private Faculty resolveOrCreateFaculty(String facultyName, ImportSummaryResponse summary) {
        return facultyRepository.findByNameIgnoreCase(facultyName)
                .map(existing -> {
                    summary.getFaculties().incrementSkipped();
                    return existing;
                })
                .orElseGet(() -> {
                    Faculty created = new Faculty();
                    created.setName(facultyName);
                    summary.getFaculties().incrementCreated();
                    return facultyRepository.save(created);
                });
    }

    private Semester resolveOrCreateSemester(
            Faculty faculty,
            ImportSemesterRequest semesterRequest,
            ImportSummaryResponse summary
    ) {
        String number = SemesterService.normalizeNumber(semesterRequest.getNumber());
        return semesterRepository.findByNumberAndFacultyId(number, faculty.getId())
                .map(existing -> {
                    summary.getSemesters().incrementSkipped();
                    return existing;
                })
                .orElseGet(() -> {
                    Semester created = new Semester();
                    created.setNumber(number);
                    created.setFaculty(faculty);
                    summary.getSemesters().incrementCreated();
                    return semesterRepository.save(created);
                });
    }

    private Subject resolveOrCreateSubject(
            Semester semester,
            ImportSubjectRequest subjectRequest,
            ImportSummaryResponse summary
    ) {
        String name = normalizeSubjectName(subjectRequest.getName());
        return subjectRepository.findByNameIgnoreCaseAndSemesterId(name, semester.getId())
                .map(existing -> {
                    summary.getSubjects().incrementSkipped();
                    return existing;
                })
                .orElseGet(() -> {
                    Subject created = new Subject();
                    created.setName(name);
                    created.setLecturer(subjectRequest.getLecturer().trim());
                    created.setType(subjectRequest.getType().trim());
                    created.setDescription(subjectRequest.getDescription());
                    created.setSemester(semester);
                    summary.getSubjects().incrementCreated();
                    return subjectRepository.save(created);
                });
    }

    private void resolveOrCreateLecture(
            Subject subject,
            ImportLectureRequest lectureRequest,
            ImportSummaryResponse summary
    ) {
        String title = normalizeLectureTitle(lectureRequest.getTitle());
        boolean exists = lectureRepository
                .findByTitleAndWeekAndTypeAndSubjectId(
                        title,
                        lectureRequest.getWeek(),
                        lectureRequest.getType(),
                        subject.getId()
                )
                .isPresent();

        if (exists) {
            summary.getLectures().incrementSkipped();
            return;
        }

        Lecture lecture = new Lecture();
        lecture.setWeek(lectureRequest.getWeek());
        lecture.setLectureNumber(lectureRequest.getLectureNumber());
        lecture.setType(lectureRequest.getType());
        lecture.setTitle(title);
        lecture.setDescription(lectureRequest.getDescription());
        lecture.setReading(lectureRequest.getReading());
        lecture.setSubject(subject);
        lectureRepository.save(lecture);
        summary.getLectures().incrementCreated();
    }

    private static String normalizeSubjectName(String name) {
        return name == null ? null : name.trim();
    }

    private static String normalizeLectureTitle(String title) {
        return title == null ? null : title.trim();
    }
}
