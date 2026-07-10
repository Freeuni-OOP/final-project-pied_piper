package com.lecturboxd.service;

import com.lecturboxd.dto.request.FacultyImportRequest;
import com.lecturboxd.dto.request.ImportLectureRequest;
import com.lecturboxd.dto.request.ImportSemesterRequest;
import com.lecturboxd.dto.request.ImportSubjectRequest;
import com.lecturboxd.dto.response.ImportSummaryResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminImportServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private AdminImportService adminImportService;

    @Test
    void importCreatesFullTreeWhenMissing() {
        FacultyImportRequest request = buildRequest();

        when(facultyRepository.findByNameIgnoreCase("CS")).thenReturn(Optional.empty());
        Faculty faculty = faculty(1L, "CS");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        when(semesterRepository.findByNumberAndFacultyId("1", 1L)).thenReturn(Optional.empty());
        Semester semester = semester(2L, "1", faculty);
        when(semesterRepository.save(any(Semester.class))).thenReturn(semester);

        when(subjectRepository.findByNameIgnoreCaseAndSemesterId("OOP", 2L)).thenReturn(Optional.empty());
        Subject subject = subject(3L, "OOP", semester);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        when(lectureRepository.findByTitleAndWeekAndTypeAndSubjectId(
                eq("Intro"), eq(1), eq(LectureType.LECTURE), eq(3L)
        )).thenReturn(Optional.empty());
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> inv.getArgument(0));

        ImportSummaryResponse summary = adminImportService.importFacultyData(request);

        assertEquals(1, summary.getFaculties().getCreated());
        assertEquals(0, summary.getFaculties().getSkipped());
        assertEquals(1, summary.getSemesters().getCreated());
        assertEquals(1, summary.getSubjects().getCreated());
        assertEquals(1, summary.getLectures().getCreated());
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void importSkipsExistingEntities() {
        FacultyImportRequest request = buildRequest();

        Faculty faculty = faculty(1L, "CS");
        when(facultyRepository.findByNameIgnoreCase("CS")).thenReturn(Optional.of(faculty));

        Semester semester = semester(2L, "1", faculty);
        when(semesterRepository.findByNumberAndFacultyId("1", 1L)).thenReturn(Optional.of(semester));

        Subject subject = subject(3L, "OOP", semester);
        when(subjectRepository.findByNameIgnoreCaseAndSemesterId("OOP", 2L)).thenReturn(Optional.of(subject));

        Lecture existing = new Lecture();
        existing.setId(4L);
        when(lectureRepository.findByTitleAndWeekAndTypeAndSubjectId(
                "Intro", 1, LectureType.LECTURE, 3L
        )).thenReturn(Optional.of(existing));

        ImportSummaryResponse summary = adminImportService.importFacultyData(request);

        assertEquals(0, summary.getFaculties().getCreated());
        assertEquals(1, summary.getFaculties().getSkipped());
        assertEquals(1, summary.getSemesters().getSkipped());
        assertEquals(1, summary.getSubjects().getSkipped());
        assertEquals(1, summary.getLectures().getSkipped());
        verify(facultyRepository, never()).save(any());
        verify(lectureRepository, never()).save(any());
    }

    @Test
    void importHandlesEmptySemesters() {
        FacultyImportRequest request = new FacultyImportRequest();
        request.setFaculty("Math");
        request.setSemesters(List.of());

        when(facultyRepository.findByNameIgnoreCase("Math")).thenReturn(Optional.empty());
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty(5L, "Math"));

        ImportSummaryResponse summary = adminImportService.importFacultyData(request);

        assertEquals(1, summary.getFaculties().getCreated());
        assertEquals(0, summary.getSemesters().getCreated());
        assertEquals(0, summary.getSubjects().getCreated());
        assertEquals(0, summary.getLectures().getCreated());
    }

    private static FacultyImportRequest buildRequest() {
        ImportLectureRequest lecture = new ImportLectureRequest();
        lecture.setWeek(1);
        lecture.setLectureNumber(1);
        lecture.setType(LectureType.LECTURE);
        lecture.setTitle(" Intro ");
        lecture.setDescription("desc");
        lecture.setReading("book");

        ImportSubjectRequest subject = new ImportSubjectRequest();
        subject.setName(" OOP ");
        subject.setLecturer(" Prof ");
        subject.setType(" Mandatory ");
        subject.setDescription("desc");
        subject.setLectures(List.of(lecture));

        ImportSemesterRequest semester = new ImportSemesterRequest();
        semester.setNumber(" 1 ");
        semester.setSubjects(List.of(subject));

        FacultyImportRequest request = new FacultyImportRequest();
        request.setFaculty(" CS ");
        request.setSemesters(List.of(semester));
        return request;
    }

    private static Faculty faculty(Long id, String name) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        return faculty;
    }

    private static Semester semester(Long id, String number, Faculty faculty) {
        Semester semester = new Semester();
        semester.setId(id);
        semester.setNumber(number);
        semester.setFaculty(faculty);
        return semester;
    }

    private static Subject subject(Long id, String name, Semester semester) {
        Subject subject = new Subject();
        subject.setId(id);
        subject.setName(name);
        subject.setSemester(semester);
        return subject;
    }
}
