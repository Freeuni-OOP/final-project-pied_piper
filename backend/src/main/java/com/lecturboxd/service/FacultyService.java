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

/**
 * EN: Manages faculty catalog entries (create, list, update, delete with child checks).
 * KA: მართავს ფაკულტეტების კატალოგს (შექმნა, სია, განახლება, წაშლა შვილების შემოწმებით).
 */
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

    /**
     * EN: Creates a faculty after ensuring the name is unique.
     * KA: ქმნის ფაკულტეტს სახელის უნიკალურობის შემოწმების შემდეგ.
     */
    @Transactional
    public FacultyResponse create(FacultyCreateRequest request) {
        // EN: Reject duplicate faculty name | KA: დუბლიკატი ფაკულტეტის სახელის უარყოფა
        String name = normalizeName(request.getName());
        if (facultyRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Faculty with name '" + name + "' already exists");
        }

        // EN: Persist new faculty | KA: ახალი ფაკულტეტის შენახვა
        Faculty faculty = new Faculty();
        faculty.setName(name);
        Faculty saved = facultyRepository.save(faculty);
        return facultyMapper.toResponse(saved);
    }

    /**
     * EN: Returns all faculties.
     * KA: აბრუნებს ყველა ფაკულტეტს.
     */
    @Transactional(readOnly = true)
    public List<FacultyResponse> findAll() {
        // EN: Load all faculties from DB | KA: ყველა ფაკულტეტის ჩატვირთვა ბაზიდან
        return facultyRepository.findAll().stream()
                .map(facultyMapper::toResponse)
                .toList();
    }

    /**
     * EN: Returns a faculty by id.
     * KA: აბრუნებს ფაკულტეტს ID-ით.
     */
    @Transactional(readOnly = true)
    public FacultyResponse findById(Long id) {
        // EN: Load faculty from DB | KA: ფაკულტეტის ჩატვირთვა ბაზიდან
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));
        return facultyMapper.toResponse(faculty);
    }

    /**
     * EN: Updates a faculty name after uniqueness validation.
     * KA: განაახლებს ფაკულტეტის სახელს უნიკალურობის შემოწმების შემდეგ.
     */
    @Transactional
    public FacultyResponse update(Long id, FacultyUpdateRequest request) {
        // EN: Load faculty from DB | KA: ფაკულტეტის ჩატვირთვა ბაზიდან
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));

        // EN: Reject duplicate name on another faculty | KA: დუბლიკატი სახელის უარყოფა სხვა ფაკულტეტზე
        String name = normalizeName(request.getName());
        if (facultyRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Faculty with name '" + name + "' already exists");
        }

        // EN: Persist updated name | KA: განახლებული სახელის შენახვა
        faculty.setName(name);
        return facultyMapper.toResponse(facultyRepository.save(faculty));
    }

    /**
     * EN: Deletes a faculty only when it has no child semesters.
     * KA: შლის ფაკულტეტს მხოლოდ მაშინ, როცა მას არ აქვს შვილობილი სემესტრები.
     */
    @Transactional
    public void delete(Long id) {
        // EN: Load faculty from DB | KA: ფაკულტეტის ჩატვირთვა ბაზიდან
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + id));

        // EN: Block delete while semesters still exist | KA: წაშლის ბლოკირება, სანამ სემესტრები არსებობს
        long semesterCount = semesterRepository.countByFacultyId(id);
        if (semesterCount > 0) {
            throw new BadRequestException(
                    "Cannot delete faculty with id " + id + ": " + semesterCount + " semester(s) still exist under it"
            );
        }

        // EN: Delete faculty from DB | KA: ფაკულტეტის წაშლა ბაზიდან
        facultyRepository.delete(faculty);
    }

    static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        return name.trim();
    }
}
