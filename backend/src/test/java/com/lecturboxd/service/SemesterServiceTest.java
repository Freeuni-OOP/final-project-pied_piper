package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.SemesterMapper;
import com.lecturboxd.dto.request.SemesterCreateRequest;
import com.lecturboxd.dto.request.SemesterUpdateRequest;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.entity.Semester;
import com.lecturboxd.exception.BadRequestException;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemesterServiceTest {

    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private SemesterMapper semesterMapper;

    @InjectMocks
    private SemesterService semesterService;

    @Test
    void createSavesUnderFaculty() {
        Faculty faculty = faculty(10L, "CS");
        when(facultyRepository.findById(10L)).thenReturn(Optional.of(faculty));
        when(semesterRepository.existsByNumberAndFacultyId("1", 10L)).thenReturn(false);
        Semester saved = semester(1L, "1", faculty);
        when(semesterRepository.save(any(Semester.class))).thenReturn(saved);
        SemesterResponse mapped = response(1L, "1", 10L);
        when(semesterMapper.toResponse(saved)).thenReturn(mapped);

        SemesterCreateRequest request = new SemesterCreateRequest();
        request.setNumber(" 1 ");

        assertEquals(mapped, semesterService.create(10L, request));
        ArgumentCaptor<Semester> captor = ArgumentCaptor.forClass(Semester.class);
        verify(semesterRepository).save(captor.capture());
        assertEquals("1", captor.getValue().getNumber());
        assertEquals(faculty, captor.getValue().getFaculty());
    }

    @Test
    void createThrowsWhenFacultyMissing() {
        when(facultyRepository.findById(99L)).thenReturn(Optional.empty());
        SemesterCreateRequest request = new SemesterCreateRequest();
        request.setNumber("1");
        assertThrows(ResourceNotFoundException.class, () -> semesterService.create(99L, request));
    }

    @Test
    void createThrowsOnDuplicate() {
        Faculty faculty = faculty(10L, "CS");
        when(facultyRepository.findById(10L)).thenReturn(Optional.of(faculty));
        when(semesterRepository.existsByNumberAndFacultyId("1", 10L)).thenReturn(true);

        SemesterCreateRequest request = new SemesterCreateRequest();
        request.setNumber("1");

        assertThrows(ConflictException.class, () -> semesterService.create(10L, request));
    }

    @Test
    void findByFacultyIdMapsList() {
        when(facultyRepository.existsById(10L)).thenReturn(true);
        Faculty faculty = faculty(10L, "CS");
        Semester semester = semester(1L, "1", faculty);
        when(semesterRepository.findByFacultyIdOrderByNumberAsc(10L)).thenReturn(List.of(semester));
        SemesterResponse mapped = response(1L, "1", 10L);
        when(semesterMapper.toResponse(semester)).thenReturn(mapped);

        assertEquals(List.of(mapped), semesterService.findByFacultyId(10L));
    }

    @Test
    void findByFacultyIdThrowsWhenFacultyMissing() {
        when(facultyRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> semesterService.findByFacultyId(10L));
    }

    @Test
    void updateChangesNumber() {
        Faculty faculty = faculty(10L, "CS");
        Semester semester = semester(1L, "1", faculty);
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(semester));
        when(semesterRepository.existsByNumberAndFacultyIdAndIdNot("2", 10L, 1L)).thenReturn(false);
        when(semesterRepository.save(semester)).thenReturn(semester);
        SemesterResponse mapped = response(1L, "2", 10L);
        when(semesterMapper.toResponse(semester)).thenReturn(mapped);

        SemesterUpdateRequest request = new SemesterUpdateRequest();
        request.setNumber(" 2 ");

        assertEquals(mapped, semesterService.update(1L, request));
        assertEquals("2", semester.getNumber());
    }

    @Test
    void updateThrowsOnConflict() {
        Faculty faculty = faculty(10L, "CS");
        Semester semester = semester(1L, "1", faculty);
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(semester));
        when(semesterRepository.existsByNumberAndFacultyIdAndIdNot("2", 10L, 1L)).thenReturn(true);

        SemesterUpdateRequest request = new SemesterUpdateRequest();
        request.setNumber("2");

        assertThrows(ConflictException.class, () -> semesterService.update(1L, request));
    }

    @Test
    void updateThrowsWhenMissing() {
        when(semesterRepository.findById(5L)).thenReturn(Optional.empty());
        SemesterUpdateRequest request = new SemesterUpdateRequest();
        request.setNumber("1");
        assertThrows(ResourceNotFoundException.class, () -> semesterService.update(5L, request));
    }

    @Test
    void deleteRemovesWhenNoSubjects() {
        Faculty faculty = faculty(10L, "CS");
        Semester semester = semester(1L, "1", faculty);
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(semester));
        when(subjectRepository.countBySemesterId(1L)).thenReturn(0L);

        semesterService.delete(1L);

        verify(semesterRepository).delete(semester);
    }

    @Test
    void deleteThrowsWhenSubjectsExist() {
        Faculty faculty = faculty(10L, "CS");
        Semester semester = semester(1L, "1", faculty);
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(semester));
        when(subjectRepository.countBySemesterId(1L)).thenReturn(3L);

        assertThrows(BadRequestException.class, () -> semesterService.delete(1L));
        verify(semesterRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(semesterRepository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> semesterService.delete(8L));
    }

    @Test
    void normalizeNumberTrimsOrNull() {
        assertNull(SemesterService.normalizeNumber(null));
        assertEquals("1", SemesterService.normalizeNumber(" 1 "));
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

    private static SemesterResponse response(Long id, String number, Long facultyId) {
        return new SemesterResponse(id, number, facultyId, 0L, LocalDateTime.now(), LocalDateTime.now());
    }
}
