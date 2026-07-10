package com.lecturboxd.service;

import com.lecturboxd.dto.request.LectureRequest;
import com.lecturboxd.dto.response.LectureResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureType;
import com.lecturboxd.entity.Subject;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceCoverageTest {

    @Mock private LectureRepository lectureRepository;
    @Mock private SubjectRepository subjectRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void createThrowsWhenSubjectMissing() {
        LectureRequest request = request(99L);
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureService.create(request));
    }

    @Test
    void getAllMapsLectures() {
        Subject subject = subject(1L);
        Lecture lecture = lecture(2L, subject);
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        List<LectureResponse> result = lectureService.getAll();
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(0).getSubjectId());
    }

    @Test
    void searchReturnsEmptyForNullQuery() {
        assertTrue(lectureService.search(null, Pageable.unpaged()).isEmpty());
    }

    @Test
    void updatePaths() {
        LectureRequest request = request(1L);
        when(lectureRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureService.update(5L, request));

        Subject subject = subject(1L);
        Lecture lecture = lecture(5L, subject);
        when(lectureRepository.findById(5L)).thenReturn(Optional.of(lecture));
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureService.update(5L, request));

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(lectureRepository.save(lecture)).thenReturn(lecture);
        LectureResponse response = lectureService.update(5L, request);
        assertEquals("New Title", response.getTitle());
        assertEquals(LectureType.LAB, response.getType());
    }

    @Test
    void deleteRemovesExisting() {
        when(lectureRepository.existsById(3L)).thenReturn(true);
        lectureService.delete(3L);
        verify(lectureRepository).deleteById(3L);
    }

    private static LectureRequest request(Long subjectId) {
        LectureRequest request = new LectureRequest();
        request.setSubjectId(subjectId);
        request.setWeek(1);
        request.setLectureNumber(2);
        request.setType(LectureType.LAB);
        request.setTitle(" New Title ");
        request.setDescription("d");
        request.setReading("r");
        return request;
    }

    private static Subject subject(Long id) {
        Subject subject = new Subject();
        subject.setId(id);
        return subject;
    }

    private static Lecture lecture(Long id, Subject subject) {
        Lecture lecture = new Lecture();
        lecture.setId(id);
        lecture.setSubject(subject);
        lecture.setWeek(1);
        lecture.setLectureNumber(1);
        lecture.setType(LectureType.LECTURE);
        lecture.setTitle("Old");
        return lecture;
    }
}
