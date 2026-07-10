package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemesterMapperTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SemesterMapper semesterMapper;

    @Test
    void toResponseIncludesSubjectCount() {
        Faculty faculty = new Faculty();
        faculty.setId(9L);

        Semester semester = new Semester();
        semester.setId(2L);
        semester.setNumber("1");
        semester.setFaculty(faculty);
        semester.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        semester.setUpdatedAt(LocalDateTime.of(2026, 1, 2, 0, 0));
        when(subjectRepository.countBySemesterId(2L)).thenReturn(5L);

        SemesterResponse response = semesterMapper.toResponse(semester);

        assertEquals(2L, response.getId());
        assertEquals("1", response.getNumber());
        assertEquals(9L, response.getFacultyId());
        assertEquals(5L, response.getSubjectCount());
    }
}
