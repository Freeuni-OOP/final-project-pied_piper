package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FacultyMapper;
import com.lecturboxd.dto.request.FacultyCreateRequest;
import com.lecturboxd.dto.request.FacultyUpdateRequest;
import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.entity.Faculty;
import com.lecturboxd.exception.BadRequestException;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.SemesterRepository;
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
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyService facultyService;

    @Test
    void createSavesTrimmedName() {
        FacultyCreateRequest request = new FacultyCreateRequest();
        request.setName("  CS  ");
        when(facultyRepository.existsByNameIgnoreCase("CS")).thenReturn(false);
        Faculty saved = faculty(1L, "CS");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(saved);
        FacultyResponse mapped = response(1L, "CS");
        when(facultyMapper.toResponse(saved)).thenReturn(mapped);

        FacultyResponse result = facultyService.create(request);

        assertEquals(mapped, result);
        ArgumentCaptor<Faculty> captor = ArgumentCaptor.forClass(Faculty.class);
        verify(facultyRepository).save(captor.capture());
        assertEquals("CS", captor.getValue().getName());
    }

    @Test
    void createThrowsOnDuplicate() {
        FacultyCreateRequest request = new FacultyCreateRequest();
        request.setName("CS");
        when(facultyRepository.existsByNameIgnoreCase("CS")).thenReturn(true);

        assertThrows(ConflictException.class, () -> facultyService.create(request));
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void findAllMapsEntities() {
        Faculty faculty = faculty(1L, "CS");
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));
        FacultyResponse mapped = response(1L, "CS");
        when(facultyMapper.toResponse(faculty)).thenReturn(mapped);

        assertEquals(List.of(mapped), facultyService.findAll());
    }

    @Test
    void findByIdReturnsMapped() {
        Faculty faculty = faculty(2L, "Math");
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(faculty));
        FacultyResponse mapped = response(2L, "Math");
        when(facultyMapper.toResponse(faculty)).thenReturn(mapped);

        assertEquals(mapped, facultyService.findById(2L));
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(facultyRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> facultyService.findById(9L));
    }

    @Test
    void updateChangesName() {
        Faculty faculty = faculty(1L, "Old");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.existsByNameIgnoreCaseAndIdNot("New", 1L)).thenReturn(false);
        when(facultyRepository.save(faculty)).thenReturn(faculty);
        FacultyResponse mapped = response(1L, "New");
        when(facultyMapper.toResponse(faculty)).thenReturn(mapped);

        FacultyUpdateRequest request = new FacultyUpdateRequest();
        request.setName(" New ");

        assertEquals(mapped, facultyService.update(1L, request));
        assertEquals("New", faculty.getName());
    }

    @Test
    void updateThrowsOnConflict() {
        Faculty faculty = faculty(1L, "Old");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.existsByNameIgnoreCaseAndIdNot("Taken", 1L)).thenReturn(true);

        FacultyUpdateRequest request = new FacultyUpdateRequest();
        request.setName("Taken");

        assertThrows(ConflictException.class, () -> facultyService.update(1L, request));
    }

    @Test
    void updateThrowsWhenMissing() {
        when(facultyRepository.findById(5L)).thenReturn(Optional.empty());
        FacultyUpdateRequest request = new FacultyUpdateRequest();
        request.setName("X");
        assertThrows(ResourceNotFoundException.class, () -> facultyService.update(5L, request));
    }

    @Test
    void deleteRemovesWhenNoSemesters() {
        Faculty faculty = faculty(1L, "CS");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(semesterRepository.countByFacultyId(1L)).thenReturn(0L);

        facultyService.delete(1L);

        verify(facultyRepository).delete(faculty);
    }

    @Test
    void deleteThrowsWhenSemestersExist() {
        Faculty faculty = faculty(1L, "CS");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(semesterRepository.countByFacultyId(1L)).thenReturn(2L);

        assertThrows(BadRequestException.class, () -> facultyService.delete(1L));
        verify(facultyRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(facultyRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> facultyService.delete(3L));
    }

    @Test
    void normalizeNameTrimsOrNull() {
        assertNull(FacultyService.normalizeName(null));
        assertEquals("CS", FacultyService.normalizeName("  CS  "));
    }

    private static Faculty faculty(Long id, String name) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        return faculty;
    }

    private static FacultyResponse response(Long id, String name) {
        return new FacultyResponse(id, name, 0L, LocalDateTime.now(), LocalDateTime.now());
    }
}
