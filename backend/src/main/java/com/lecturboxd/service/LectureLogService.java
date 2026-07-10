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

/**
 * EN: Manages lecture watch logs (create, list, fetch, delete) and related feed activity.
 * KA: მართავს ლექციის ნახვის ლოგებს (შექმნა, სია, მოძიება, წაშლა) და დაკავშირებულ ფიდის აქტივობას.
 */
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

    /**
     * EN: Creates a lecture log for the user if one does not already exist for that lecture.
     * KA: ქმნის ლექციის ლოგს მომხმარებლისთვის, თუ ამ ლექციისთვის უკვე არ არსებობს.
     */
    @Transactional
    public LectureLogResponse createLog(UUID userId, Long lectureId, LectureLogRequest request) {
        // EN: Load lecture and user from DB | KA: ლექციისა და მომხმარებლის ჩატვირთვა ბაზიდან
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id " + lectureId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // EN: Reject duplicate log for same user+lecture | KA: დუბლიკატი ლოგის უარყოფა იგივე მომხმარებელი+ლექცია
        if (lectureLogRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new ConflictException("You have already logged this lecture");
        }

        // EN: Build and persist lecture log | KA: ლექციის ლოგის აგება და შენახვა
        LectureLog lectureLog = new LectureLog();
        lectureLog.setUser(user);
        lectureLog.setLecture(lecture);
        lectureLog.setWatchedAt(request.getWatchedAt() != null ? request.getWatchedAt() : LocalDate.now());

        LectureLog saved = lectureLogRepository.save(lectureLog);
        // EN: Side effect — record LECTURE_LOGGED activity for feed | KA: გვერდითი ეფექტი — LECTURE_LOGGED აქტივობის ჩაწერა ფიდისთვის
        activityService.recordLectureLogged(saved);
        return feedMapper.toResponse(saved);
    }

    /**
     * EN: Returns a paginated list of lecture logs for the given user.
     * KA: აბრუნებს მოცემული მომხმარებლის ლექციის ლოგების გვერდებად დაყოფილ სიას.
     */
    @Transactional(readOnly = true)
    public Page<LectureLogResponse> getLogsByUser(UUID userId, Pageable pageable) {
        // EN: Ensure user exists | KA: მომხმარებლის არსებობის შემოწმება
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        // EN: Query logs ordered by watch date | KA: ლოგების მოთხოვნა ნახვის თარიღით დალაგებით
        return lectureLogRepository.findByUserIdOrderByWatchedAtDescCreatedAtDesc(userId, pageable)
                .map(feedMapper::toResponse);
    }

    /**
     * EN: Returns the current user's log for a specific lecture, if present.
     * KA: აბრუნებს მიმდინარე მომხმარებლის ლოგს კონკრეტული ლექციისთვის, თუ არსებობს.
     */
    @Transactional(readOnly = true)
    public LectureLogResponse getMyLog(UUID userId, Long lectureId) {
        // EN: Ensure lecture exists | KA: ლექციის არსებობის შემოწმება
        if (!lectureRepository.existsById(lectureId)) {
            throw new ResourceNotFoundException("Lecture not found with id " + lectureId);
        }
        // EN: Load user+lecture log from DB | KA: მომხმარებელი+ლექცია ლოგის ჩატვირთვა ბაზიდან
        LectureLog lectureLog = lectureLogRepository.findByUserIdAndLectureId(userId, lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture is not logged"));
        return feedMapper.toResponse(lectureLog);
    }

    /**
     * EN: Deletes the current user's log for a lecture after ownership check.
     * KA: შლის მიმდინარე მომხმარებლის ლოგს ლექციისთვის მფლობელობის შემოწმების შემდეგ.
     */
    @Transactional
    public void deleteMyLog(UUID userId, Long lectureId) {
        // EN: Load log by user+lecture | KA: ლოგის ჩატვირთვა მომხმარებელი+ლექციით
        LectureLog lectureLog = lectureLogRepository.findByUserIdAndLectureId(userId, lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture is not logged"));
        // EN: Ownership validation | KA: მფლობელობის ვალიდაცია
        if (!lectureLog.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this lecture log");
        }
        // EN: Delete log from DB | KA: ლოგის წაშლა ბაზიდან
        lectureLogRepository.delete(lectureLog);
    }

    /**
     * EN: Deletes a lecture log by id after verifying the caller owns it.
     * KA: შლის ლექციის ლოგს ID-ით, მას შემდეგ რაც ამოწმებს, რომ გამომძახებელი მფლობელია.
     */
    @Transactional
    public void deleteLog(UUID userId, Long logId) {
        // EN: Load log by id | KA: ლოგის ჩატვირთვა ID-ით
        LectureLog lectureLog = lectureLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture log not found with id " + logId));

        // EN: Ownership validation | KA: მფლობელობის ვალიდაცია
        if (!lectureLog.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this lecture log");
        }

        // EN: Delete log from DB | KA: ლოგის წაშლა ბაზიდან
        lectureLogRepository.delete(lectureLog);
    }
}
