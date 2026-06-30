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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final SemesterRepository semesterRepository;
    private final FacultyMapper facultyMapper;

    public FacultyService(
            FacultyRepository facultyRepository,
            SemesterRepository semesterRepository,
            FacultyMapper facultyMapper
    ) {
        this.facultyRepository = facultyRepository;
        this.semesterRepository = semesterRepository;
        this.facultyMapper = facultyMapper;
    }

    @Transactional
    public FacultyResponse create(FacultyCreateRequest request) {
        String name = normalizeName(request.getName());
        if (facultyRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Faculty with name '" + name + "' already exists");
        }

        Faculty faculty = new Faculty();
        faculty.setName(name);
        Faculty saved = facultyRepository.save(faculty);
        return facultyMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FacultyResponse> findAll() {
        return facultyRepository.findAll().stream()
                .map(facultyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacultyResponse findById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));
        return facultyMapper.toResponse(faculty);
    }

    @Transactional
    public FacultyResponse update(Long id, FacultyUpdateRequest request) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));

        String name = normalizeName(request.getName());
        if (facultyRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Faculty with name '" + name + "' already exists");
        }

        faculty.setName(name);
        return facultyMapper.toResponse(facultyRepository.save(faculty));
    }

    @Transactional
    public void delete(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));

        long semesterCount = semesterRepository.countByFacultyId(id);
        if (semesterCount > 0) {
            throw new BadRequestException(
                    "Cannot delete faculty with id " + id + ": " + semesterCount + " semester(s) still exist under it"
            );
        }

        facultyRepository.delete(faculty);
    }

    static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        return name.trim();
    }
}
