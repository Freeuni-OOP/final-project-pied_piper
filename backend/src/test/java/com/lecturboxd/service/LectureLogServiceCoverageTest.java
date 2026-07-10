package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureLogServiceCoverageTest {

    @Mock private LectureLogRepository lectureLogRepository;
    @Mock private LectureRepository lectureRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActivityService activityService;
    @Mock private FeedMapper feedMapper;

    @InjectMocks
    private LectureLogService lectureLogService;

    @Test
    void createLogMissingEntitiesAndCustomDate() {
        UUID userId = UUID.randomUUID();
        LectureLogRequest request = new LectureLogRequest();
        request.setWatchedAt(LocalDate.of(2026, 1, 1));

        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> lectureLogService.createLog(userId, 1L, request));

        Lecture lecture = lecture(1L);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> lectureLogService.createLog(userId, 1L, request));

        User user = user(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureLogRepository.existsByUserIdAndLectureId(userId, 1L)).thenReturn(false);
        when(lectureLogRepository.save(any(LectureLog.class))).thenAnswer(inv -> {
            LectureLog log = inv.getArgument(0);
            log.setId(7L);
            return log;
        });
        LectureLogResponse mapped = new LectureLogResponse();
        when(feedMapper.toResponse(any(LectureLog.class))).thenReturn(mapped);

        assertEquals(mapped, lectureLogService.createLog(userId, 1L, request));
        verify(activityService).recordLectureLogged(any(LectureLog.class));
    }

    @Test
    void getLogsByUserAndMyLog() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> lectureLogService.getLogsByUser(userId, Pageable.unpaged()));

        LectureLog log = log(5L, userId, lecture(1L));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(lectureLogRepository.findByUserIdOrderByWatchedAtDescCreatedAtDesc(userId, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(log)));
        LectureLogResponse mapped = new LectureLogResponse();
        when(feedMapper.toResponse(log)).thenReturn(mapped);
        assertEquals(1, lectureLogService.getLogsByUser(userId, Pageable.unpaged()).getTotalElements());

        when(lectureRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> lectureLogService.getMyLog(userId, 1L));

        when(lectureRepository.existsById(1L)).thenReturn(true);
        when(lectureLogRepository.findByUserIdAndLectureId(userId, 1L)).thenReturn(Optional.of(log));
        assertEquals(mapped, lectureLogService.getMyLog(userId, 1L));
    }

    @Test
    void deleteMyLogMissing() {
        UUID userId = UUID.randomUUID();
        when(lectureLogRepository.findByUserIdAndLectureId(userId, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureLogService.deleteMyLog(userId, 1L));
    }

    @Test
    void deleteLogPaths() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        LectureLog log = log(5L, owner, lecture(1L));

        when(lectureLogRepository.findById(5L)).thenReturn(Optional.of(log));
        assertThrows(ForbiddenException.class, () -> lectureLogService.deleteLog(other, 5L));
        verify(lectureLogRepository, never()).delete(any());

        lectureLogService.deleteLog(owner, 5L);
        verify(lectureLogRepository).delete(log);

        when(lectureLogRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lectureLogService.deleteLog(owner, 99L));
    }

    private static User user(UUID id) {
        User user = new User();
        user.setId(id);
        user.setName("U");
        return user;
    }

    private static Lecture lecture(Long id) {
        Lecture lecture = new Lecture();
        lecture.setId(id);
        lecture.setTitle("T");
        return lecture;
    }

    private static LectureLog log(Long id, UUID userId, Lecture lecture) {
        LectureLog log = new LectureLog();
        log.setId(id);
        log.setUser(user(userId));
        log.setLecture(lecture);
        log.setWatchedAt(LocalDate.now());
        return log;
    }
}
