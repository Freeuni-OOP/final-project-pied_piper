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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void getByIdThrowsWhenMissing() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureService.getById(1L));
    }

    @Test
    void getByIdReturnsMappedResponse() {
        Subject subject = new Subject();
        subject.setId(3L);
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setSubject(subject);
        lecture.setTitle("Intro");
        lecture.setType(LectureType.LECTURE);
        lecture.setWeek(1);
        lecture.setLectureNumber(1);

        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        LectureResponse response = lectureService.getById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Intro", response.getTitle());
        assertEquals(3L, response.getSubjectId());
    }

    @Test
    void searchReturnsEmptyForBlankQuery() {
        Page<LectureResponse> page = lectureService.search("  ", PageRequest.of(0, 10));
        assertTrue(page.isEmpty());
    }

    @Test
    void searchDelegatesToRepository() {
        Subject subject = new Subject();
        subject.setId(1L);
        Lecture lecture = new Lecture();
        lecture.setId(5L);
        lecture.setSubject(subject);
        lecture.setTitle("Algorithms");
        lecture.setType(LectureType.LECTURE);

        when(lectureRepository.searchByTitleOrDescription("algo", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(lecture)));

        Page<LectureResponse> page = lectureService.search("algo", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Algorithms", page.getContent().get(0).getTitle());
    }

    @Test
    void createPersistsLectureForSubject() {
        Subject subject = new Subject();
        subject.setId(2L);
        LectureRequest request = new LectureRequest();
        request.setSubjectId(2L);
        request.setTitle("  Week 1  ");
        request.setType(LectureType.LECTURE);
        request.setWeek(1);
        request.setLectureNumber(1);

        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> {
            Lecture l = inv.getArgument(0);
            l.setId(100L);
            return l;
        });

        LectureResponse response = lectureService.create(request);

        assertEquals(100L, response.getId());
        assertEquals("Week 1", response.getTitle());
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(lectureRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> lectureService.delete(9L));
    }
}
