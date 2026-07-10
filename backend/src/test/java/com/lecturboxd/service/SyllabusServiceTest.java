package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FacultyMapper;
import com.lecturboxd.dto.mapper.LectureMapper;
import com.lecturboxd.dto.mapper.SemesterMapper;
import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.entity.Faculty;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyllabusServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private FacultyMapper facultyMapper;
    @Mock
    private SemesterMapper semesterMapper;
    @Mock
    private LectureMapper lectureMapper;

    @InjectMocks
    private SyllabusService syllabusService;

    @Test
    void listFacultiesMapsAll() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("MACS");
        FacultyResponse response = new FacultyResponse(1L, "MACS", 0, null, null);

        when(facultyRepository.findAll()).thenReturn(List.of(faculty));
        when(facultyMapper.toResponse(faculty)).thenReturn(response);

        List<FacultyResponse> result = syllabusService.listFaculties();

        assertEquals(1, result.size());
        assertEquals("MACS", result.get(0).getName());
    }

    @Test
    void listSemestersThrowsWhenFacultyMissing() {
        when(facultyRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> syllabusService.listSemesters(99L));
    }

    @Test
    void listSubjectsThrowsWhenSemesterMissing() {
        when(semesterRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> syllabusService.listSubjects(99L));
    }
}
