package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureLogServiceExtendedTest {

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
    void createLogRecordsActivityAndDefaultsWatchedAt() {
        UUID userId = UUID.randomUUID();
        Long lectureId = 5L;
        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setTitle("T");
        User user = new User();
        user.setId(userId);

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureLogRepository.existsByUserIdAndLectureId(userId, lectureId)).thenReturn(false);
        when(lectureLogRepository.save(any(LectureLog.class))).thenAnswer(inv -> {
            LectureLog log = inv.getArgument(0);
            log.setId(8L);
            return log;
        });
        when(feedMapper.toResponse(any(LectureLog.class))).thenReturn(
                new LectureLogResponse(8L, userId, lectureId, "T", LocalDate.now(), null)
        );

        LectureLogResponse response = lectureLogService.createLog(userId, lectureId, new LectureLogRequest());

        assertEquals(8L, response.getId());
        ArgumentCaptor<LectureLog> captor = ArgumentCaptor.forClass(LectureLog.class);
        verify(lectureLogRepository).save(captor.capture());
        assertEquals(LocalDate.now(), captor.getValue().getWatchedAt());
        verify(activityService).recordLectureLogged(any(LectureLog.class));
    }

    @Test
    void getMyLogThrowsWhenNotLogged() {
        UUID userId = UUID.randomUUID();
        when(lectureRepository.existsById(1L)).thenReturn(true);
        when(lectureLogRepository.findByUserIdAndLectureId(userId, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lectureLogService.getMyLog(userId, 1L));
    }

    @Test
    void deleteMyLogRemovesExistingLog() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        LectureLog log = new LectureLog();
        log.setId(3L);
        log.setUser(user);

        when(lectureLogRepository.findByUserIdAndLectureId(userId, 9L)).thenReturn(Optional.of(log));

        lectureLogService.deleteMyLog(userId, 9L);

        verify(lectureLogRepository).delete(log);
    }
}
