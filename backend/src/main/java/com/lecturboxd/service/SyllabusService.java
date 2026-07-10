package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FacultyMapper;
import com.lecturboxd.dto.mapper.LectureMapper;
import com.lecturboxd.dto.response.FacultyResponse;
import com.lecturboxd.dto.response.LectureSessionResponse;
import com.lecturboxd.dto.response.SemesterResponse;
import com.lecturboxd.dto.response.SubjectSummaryResponse;
import com.lecturboxd.dto.response.SubjectSyllabusResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.FacultyRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SemesterRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * EN: Read-only syllabus navigation: faculties, semesters, subjects, and typed session lists.
 * KA: მხოლოდ წაკითხვადი სილაბუსის ნავიგაცია: ფაკულტეტები, სემესტრები, საგნები და ტიპიზებული სესიების სიები.
 */
@Service
public class SyllabusService {

    private final FacultyRepository facultyRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final LectureRepository lectureRepository;
    private final FacultyMapper facultyMapper;
    private final com.lecturboxd.dto.mapper.SemesterMapper semesterMapper;
    private final LectureMapper lectureMapper;

    public SyllabusService(
            FacultyRepository facultyRepository,
            SemesterRepository semesterRepository,
            SubjectRepository subjectRepository,
            LectureRepository lectureRepository,
            FacultyMapper facultyMapper,
            com.lecturboxd.dto.mapper.SemesterMapper semesterMapper,
            LectureMapper lectureMapper
    ) {
        this.facultyRepository = facultyRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.lectureRepository = lectureRepository;
        this.facultyMapper = facultyMapper;
        this.semesterMapper = semesterMapper;
        this.lectureMapper = lectureMapper;
    }

    /**
     * EN: Lists all faculties for syllabus browsing.
     * KA: აბრუნებს ყველა ფაკულტეტს სილაბუსის დათვალიერებისთვის.
     */
    @Transactional(readOnly = true)
    public List<FacultyResponse> listFaculties() {
        // EN: Load all faculties from DB | KA: ყველა ფაკულტეტის ჩატვირთვა ბაზიდან
        return facultyRepository.findAll().stream()
                .map(facultyMapper::toResponse)
                .toList();
    }

    /**
     * EN: Lists semesters for a faculty ordered by number.
     * KA: აბრუნებს ფაკულტეტის სემესტრებს ნომრით დალაგებით.
     */
    @Transactional(readOnly = true)
    public List<SemesterResponse> listSemesters(Long facultyId) {
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
     * EN: Lists subject summaries for a semester ordered by name.
     * KA: აბრუნებს სემესტრის საგნების შეჯამებებს სახელით დალაგებით.
     */
    @Transactional(readOnly = true)
    public List<SubjectSummaryResponse> listSubjects(Long semesterId) {
        // EN: Ensure semester exists | KA: სემესტრის არსებობის შემოწმება
        if (!semesterRepository.existsById(semesterId)) {
            throw new ResourceNotFoundException("Semester not found with id " + semesterId);
        }
        // EN: Query subjects by semester | KA: საგნების მოთხოვნა სემესტრით
        return subjectRepository.findBySemesterIdOrderByNameAsc(semesterId).stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * EN: Builds a full subject syllabus with sessions grouped by type.
     * KA: აგებს სრულ საგნის სილაბუსს სესიებით ტიპის მიხედვით დაჯგუფებით.
     */
    @Transactional(readOnly = true)
    public SubjectSyllabusResponse getSubjectSyllabus(Long subjectId) {
        // EN: Load subject from DB | KA: საგნის ჩატვირთვა ბაზიდან
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + subjectId));

        // EN: Load ordered sessions for the subject | KA: საგნის დალაგებული სესიების ჩატვირთვა
        List<Lecture> sessions = lectureRepository.findBySubjectIdOrderByWeekAscLectureNumberAscTypeAsc(subjectId);

        // EN: Initialize typed session buckets | KA: ტიპიზებული სესიების ჯგუფების ინიციალიზაცია
        SubjectSyllabusResponse response = new SubjectSyllabusResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setLecturer(subject.getLecturer());
        response.setType(subject.getType());
        response.setDescription(subject.getDescription());
        response.setSemesterId(subject.getSemester().getId());
        response.setLectures(new ArrayList<>());
        response.setSeminars(new ArrayList<>());
        response.setLabs(new ArrayList<>());
        response.setExams(new ArrayList<>());
        response.setDeadlines(new ArrayList<>());
        response.setPresentations(new ArrayList<>());

        // EN: Bucket each session by LectureType | KA: თითოეული სესიის დაჯგუფება LectureType-ის მიხედვით
        for (Lecture session : sessions) {
            LectureSessionResponse item = lectureMapper.toSessionResponse(session);
            switch (session.getType()) {
                case LECTURE -> response.getLectures().add(item);
                case SEMINAR -> response.getSeminars().add(item);
                case LAB -> response.getLabs().add(item);
                case EXAM -> response.getExams().add(item);
                case DEADLINE -> response.getDeadlines().add(item);
                case PRESENTATION -> response.getPresentations().add(item);
            }
        }

        return response;
    }

    private SubjectSummaryResponse toSummary(Subject subject) {
        return new SubjectSummaryResponse(
                subject.getId(),
                subject.getName(),
                subject.getLecturer(),
                subject.getType()
        );
    }
}
