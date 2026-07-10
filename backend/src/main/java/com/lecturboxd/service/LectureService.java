package com.lecturboxd.service;

import com.lecturboxd.dto.request.LectureRequest;
import com.lecturboxd.dto.response.LectureResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final SubjectRepository subjectRepository;

    public LectureService(LectureRepository lectureRepository, SubjectRepository subjectRepository) {
        this.lectureRepository = lectureRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public LectureResponse create(LectureRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + request.getSubjectId()));

        Lecture lecture = new Lecture();
        apply(lecture, request, subject);
        return toResponse(lectureRepository.save(lecture));
    }

    @Transactional(readOnly = true)
    public LectureResponse getById(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + id));
        return toResponse(lecture);
    }

    @Transactional(readOnly = true)
    public List<LectureResponse> getAll() {
        return lectureRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> search(String query, Pageable pageable) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return Page.empty(pageable);
        }
        return lectureRepository.searchByTitleOrDescription(trimmed, pageable).map(this::toResponse);
    }

    @Transactional
    public LectureResponse update(Long id, LectureRequest request) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + id));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id " + request.getSubjectId()));

        apply(lecture, request, subject);
        return toResponse(lectureRepository.save(lecture));
    }

    @Transactional
    public void delete(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lecture not found with id " + id);
        }
        lectureRepository.deleteById(id);
    }

    private void apply(Lecture lecture, LectureRequest request, Subject subject) {
        lecture.setSubject(subject);
        lecture.setWeek(request.getWeek());
        lecture.setLectureNumber(request.getLectureNumber());
        lecture.setType(request.getType());
        lecture.setTitle(request.getTitle().trim());
        lecture.setDescription(request.getDescription());
        lecture.setReading(request.getReading());
    }

    private LectureResponse toResponse(Lecture lecture) {
        return new LectureResponse(
                lecture.getId(),
                lecture.getSubject().getId(),
                lecture.getWeek(),
                lecture.getLectureNumber(),
                lecture.getType(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getReading()
        );
    }
}
