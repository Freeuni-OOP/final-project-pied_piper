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

/**
 * EN: Manages semesters under faculties (create, list, update, delete with child checks).
 * KA: მართავს სემესტრებს ფაკულტეტების ქვეშ (შექმნა, სია, განახლება, წაშლა შვილების შემოწმებით).
 */
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

    /**
     * EN: Creates a semester for a faculty after uniqueness validation.
     * KA: ქმნის სემესტრს ფაკულტეტისთვის უნიკალურობის შემოწმების შემდეგ.
     */
    @Transactional
    public SemesterResponse create(Long facultyId, SemesterCreateRequest request) {
        // EN: Load parent faculty from DB | KA: მშობელი ფაკულტეტის ჩატვირთვა ბაზიდან
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id " + facultyId));

        // EN: Reject duplicate semester number within faculty | KA: დუბლიკატი სემესტრის ნომრის უარყოფა ფაკულტეტში
        String number = normalizeNumber(request.getNumber());
        if (semesterRepository.existsByNumberAndFacultyId(number, facultyId)) {
            throw new ConflictException(
                    "Semester '" + number + "' already exists for faculty '" + faculty.getName() + "'"
            );
        }

        // EN: Persist new semester | KA: ახალი სემესტრის შენახვა
        Semester semester = new Semester();
        semester.setNumber(number);
        semester.setFaculty(faculty);
        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    /**
     * EN: Lists semesters for a faculty ordered by number.
     * KA: აბრუნებს ფაკულტეტის სემესტრებს ნომრით დალაგებით.
     */
    @Transactional(readOnly = true)
    public List<SemesterResponse> findByFacultyId(Long facultyId) {
        // EN: Ensure faculty exists | KA: ფაკულტეტის არსებობის შემოწმება
        if (!facultyRepository.existsById(facultyId)) {
            throw new ResourceNotFoundException("Faculty not found with id " + facultyId);
        }

        // EN: Query semesters by faculty | KA: სემესტრების მოთხოვნა ფაკულტეტით
        return semesterRepository.findByFacultyIdOrderByNumberAsc(facultyId).stream()
                .map(semesterMapper::toResponse)
                .toList();
    }

    /**
     * EN: Updates a semester number after uniqueness validation within its faculty.
     * KA: განაახლებს სემესტრის ნომერს უნიკალურობის შემოწმების შემდეგ მის ფაკულტეტში.
     */
    @Transactional
    public SemesterResponse update(Long id, SemesterUpdateRequest request) {
        // EN: Load semester from DB | KA: სემესტრის ჩატვირთვა ბაზიდან
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id " + id));

        // EN: Reject duplicate number for another semester in same faculty | KA: დუბლიკატი ნომრის უარყოფა იგივე ფაკულტეტში
        String number = normalizeNumber(request.getNumber());
        Long facultyId = semester.getFaculty().getId();
        if (semesterRepository.existsByNumberAndFacultyIdAndIdNot(number, facultyId, id)) {
            throw new ConflictException(
                    "Semester '" + number + "' already exists for this faculty"
            );
        }

        // EN: Persist updated number | KA: განახლებული ნომრის შენახვა
        semester.setNumber(number);
        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    /**
     * EN: Deletes a semester only when it has no child subjects.
     * KA: შლის სემესტრს მხოლოდ მაშინ, როცა მას არ აქვს შვილობილი საგნები.
     */
    @Transactional
    public void delete(Long id) {
        // EN: Load semester from DB | KA: სემესტრის ჩატვირთვა ბაზიდან
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id " + id));

        // EN: Block delete while subjects still exist | KA: წაშლის ბლოკირება, სანამ საგნები არსებობს
        long subjectCount = subjectRepository.countBySemesterId(id);
        if (subjectCount > 0) {
            throw new BadRequestException(
                    "Cannot delete semester with id " + id + ": " + subjectCount + " subject(s) still exist under it"
            );
        }

        // EN: Delete semester from DB | KA: სემესტრის წაშლა ბაზიდან
        semesterRepository.delete(semester);
    }

    static String normalizeNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim();
    }
}
