package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.request.LectureLogRequest;
import com.lecturboxd.dto.response.LectureLogResponse;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.LectureLog;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.ConflictException;
import com.lecturboxd.exception.ForbiddenException;
import com.lecturboxd.exception.ResourceNotFoundException;
import com.lecturboxd.repository.LectureLogRepository;
import com.lecturboxd.repository.LectureRepository;
import com.lecturboxd.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class LectureLogService {

    private final LectureLogRepository lectureLogRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;
    private final FeedMapper feedMapper;

    public LectureLogService(
            LectureLogRepository lectureLogRepository,
            LectureRepository lectureRepository,
            UserRepository userRepository,
            ActivityService activityService,
            FeedMapper feedMapper
    ) {
        this.lectureLogRepository = lectureLogRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
        this.feedMapper = feedMapper;
    }

    @Transactional
    public LectureLogResponse createLog(UUID userId, Long lectureId, LectureLogRequest request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + lectureId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (lectureLogRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new ConflictException("You have already logged this lecture");
        }

        LectureLog lectureLog = new LectureLog();
        lectureLog.setUser(user);
        lectureLog.setLecture(lecture);
        lectureLog.setWatchedAt(request.getWatchedAt() != null ? request.getWatchedAt() : LocalDate.now());

        LectureLog saved = lectureLogRepository.save(lectureLog);
        activityService.recordLectureLogged(saved);
        return feedMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LectureLogResponse> getLogsByUser(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return lectureLogRepository.findByUserIdOrderByWatchedAtDescCreatedAtDesc(userId, pageable)
                .map(feedMapper::toResponse);
    }

    @Transactional
    public void deleteLog(UUID userId, Long logId) {
        LectureLog lectureLog = lectureLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture log not found with id " + logId));

        if (!lectureLog.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this lecture log");
        }

        lectureLogRepository.delete(lectureLog);
    }
}
