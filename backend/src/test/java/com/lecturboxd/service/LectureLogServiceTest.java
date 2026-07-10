package com.lecturboxd.service;

import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.UserRepository;
import com.lecturboxd.dto.mapper.FeedMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureLogServiceTest {

    @Mock
    private LectureLogRepository lectureLogRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityService activityService;

    @Mock
    private FeedMapper feedMapper;

    @InjectMocks
    private LectureLogService lectureLogService;

    @Test
    void createLogThrowsConflictWhenLogAlreadyExists() {
        UUID userId = UUID.randomUUID();
        Long lectureId = 1L;

        Lecture lecture = new Lecture();
        lecture.setId(lectureId);

        User user = new User();
        user.setId(userId);

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureLogRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> lectureLogService.createLog(userId, lectureId, new LectureLogRequest())
        );

        assertEquals("You have already logged this lecture", exception.getMessage());
        verify(lectureLogRepository, never()).save(any(LectureLog.class));
    }
}
