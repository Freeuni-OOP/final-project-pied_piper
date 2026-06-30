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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterMapper semesterMapper;

    public SemesterService(
            SemesterRepository semesterRepository,
            FacultyRepository facultyRepository,
            SubjectRepository subjectRepository,
            SemesterMapper semesterMapper
    ) {
        this.semesterRepository = semesterRepository;
        this.facultyRepository = facultyRepository;
        this.subjectRepository = subjectRepository;
        this.semesterMapper = semesterMapper;
    }

    @Transactional
    public SemesterResponse create(Long facultyId, SemesterCreateRequest request) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + facultyId));

        String number = normalizeNumber(request.getNumber());
        if (semesterRepository.existsByNumberAndFacultyId(number, facultyId)) {
            throw new ConflictException(
                    "Semester '" + number + "' already exists for faculty '" + faculty.getName() + "'"
            );
        }

        Semester semester = new Semester();
        semester.setNumber(number);
        semester.setFaculty(faculty);
        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> findByFacultyId(Long facultyId) {
        if (!facultyRepository.existsById(facultyId)) {
            throw new ResourceNotFoundException("Faculty not found with id " + facultyId);
        }

        return semesterRepository.findByFacultyIdOrderByNumberAsc(facultyId).stream()
                .map(semesterMapper::toResponse)
                .toList();
    }

    @Transactional
    public SemesterResponse update(Long id, SemesterUpdateRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id " + id));

        String number = normalizeNumber(request.getNumber());
        Long facultyId = semester.getFaculty().getId();
        if (semesterRepository.existsByNumberAndFacultyIdAndIdNot(number, facultyId, id)) {
            throw new ConflictException(
                    "Semester '" + number + "' already exists for this faculty"
            );
        }

        semester.setNumber(number);
        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    @Transactional
    public void delete(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id " + id));

        long subjectCount = subjectRepository.countBySemesterId(id);
        if (subjectCount > 0) {
            throw new BadRequestException(
                    "Cannot delete semester with id " + id + ": " + subjectCount + " subject(s) still exist under it"
            );
        }

        semesterRepository.delete(semester);
    }

    static String normalizeNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim();
    }
}
