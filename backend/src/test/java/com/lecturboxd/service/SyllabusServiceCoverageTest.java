package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FacultyMapper;
import com.lecturboxd.dto.mapper.LectureMapper;
import com.lecturboxd.dto.mapper.SemesterMapper;
import com.lecturboxd.dto.response.LectureSessionResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.dto.response.SubjectSummaryResponse;
import com.lecturboxd.dto.response.SubjectSyllabusResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyllabusServiceCoverageTest {

    @Mock private FacultyRepository facultyRepository;
    @Mock private SemesterRepository semesterRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private LectureRepository lectureRepository;
    @Mock private FacultyMapper facultyMapper;
    @Mock private SemesterMapper semesterMapper;
    @Mock private LectureMapper lectureMapper;

    @InjectMocks
    private SyllabusService syllabusService;

    @Test
    void listSemestersMapsWhenFacultyExists() {
        when(facultyRepository.existsById(1L)).thenReturn(true);
        Semester semester = new Semester();
        semester.setId(2L);
        when(semesterRepository.findByFacultyIdOrderByNumberAsc(1L)).thenReturn(List.of(semester));
        SemesterResponse mapped = new SemesterResponse();
        when(semesterMapper.toResponse(semester)).thenReturn(mapped);

        assertEquals(List.of(mapped), syllabusService.listSemesters(1L));
    }

    @Test
    void listSubjectsMapsSummaries() {
        when(semesterRepository.existsById(2L)).thenReturn(true);
        Subject subject = new Subject();
        subject.setId(3L);
        subject.setName("OOP");
        subject.setLecturer("Prof");
        subject.setType("Mandatory");
        when(subjectRepository.findBySemesterIdOrderByNameAsc(2L)).thenReturn(List.of(subject));

        List<SubjectSummaryResponse> result = syllabusService.listSubjects(2L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals("OOP", result.get(0).getName());
        assertEquals("Prof", result.get(0).getLecturer());
        assertEquals("Mandatory", result.get(0).getType());
    }

    @Test
    void getSubjectSyllabusThrowsWhenMissing() {
        when(subjectRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> syllabusService.getSubjectSyllabus(9L));
    }

    @Test
    void getSubjectSyllabusBucketsAllTypes() {
        Semester semester = new Semester();
        semester.setId(10L);
        Subject subject = new Subject();
        subject.setId(5L);
        subject.setName("OOP");
        subject.setLecturer("Prof");
        subject.setType("Mandatory");
        subject.setDescription("desc");
        subject.setSemester(semester);

        when(subjectRepository.findById(5L)).thenReturn(Optional.of(subject));

        List<Lecture> sessions = Arrays.stream(LectureType.values())
                .map(type -> {
                    Lecture lecture = new Lecture();
                    lecture.setId((long) type.ordinal() + 1);
                    lecture.setType(type);
                    lecture.setTitle(type.name());
                    return lecture;
                })
                .toList();
        when(lectureRepository.findBySubjectIdOrderByWeekAscLectureNumberAscTypeAsc(5L)).thenReturn(sessions);
        when(lectureMapper.toSessionResponse(any(Lecture.class))).thenAnswer(inv -> {
            Lecture lecture = inv.getArgument(0);
            return new LectureSessionResponse(
                    lecture.getId(), 1, 1, lecture.getType(), lecture.getTitle(), null, null);
        });

        SubjectSyllabusResponse response = syllabusService.getSubjectSyllabus(5L);

        assertEquals(5L, response.getId());
        assertEquals("OOP", response.getName());
        assertEquals("Prof", response.getLecturer());
        assertEquals("Mandatory", response.getType());
        assertEquals("desc", response.getDescription());
        assertEquals(10L, response.getSemesterId());
        assertEquals(1, response.getLectures().size());
        assertEquals(1, response.getSeminars().size());
        assertEquals(1, response.getLabs().size());
        assertEquals(1, response.getExams().size());
        assertEquals(1, response.getDeadlines().size());
        assertEquals(1, response.getPresentations().size());
    }

    @Test
    void getSubjectSyllabusEmptyBuckets() {
        Semester semester = new Semester();
        semester.setId(10L);
        Subject subject = new Subject();
        subject.setId(5L);
        subject.setName("OOP");
        subject.setLecturer("Prof");
        subject.setType("Mandatory");
        subject.setSemester(semester);
        when(subjectRepository.findById(5L)).thenReturn(Optional.of(subject));
        when(lectureRepository.findBySubjectIdOrderByWeekAscLectureNumberAscTypeAsc(5L)).thenReturn(List.of());

        SubjectSyllabusResponse response = syllabusService.getSubjectSyllabus(5L);

        assertTrue(response.getLectures().isEmpty());
        assertTrue(response.getSeminars().isEmpty());
        assertTrue(response.getLabs().isEmpty());
        assertTrue(response.getExams().isEmpty());
        assertTrue(response.getDeadlines().isEmpty());
        assertTrue(response.getPresentations().isEmpty());
    }
}
