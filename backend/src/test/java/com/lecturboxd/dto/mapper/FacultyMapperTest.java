package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.repository.SemesterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyMapperTest {

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private FacultyMapper facultyMapper;

    @Test
    void toResponseIncludesSemesterCount() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("CS");
        faculty.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        faculty.setUpdatedAt(LocalDateTime.of(2026, 1, 2, 0, 0));
        when(semesterRepository.countByFacultyId(1L)).thenReturn(4L);

        FacultyResponse response = facultyMapper.toResponse(faculty);

        assertEquals(1L, response.getId());
        assertEquals("CS", response.getName());
        assertEquals(4L, response.getSemesterCount());
        assertEquals(faculty.getCreatedAt(), response.getCreatedAt());
        assertEquals(faculty.getUpdatedAt(), response.getUpdatedAt());
    }
}
