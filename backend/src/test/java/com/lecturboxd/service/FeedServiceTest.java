package com.lecturboxd.service;

import com.lecturboxd.dto.mapper.FeedMapper;
import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.entity.Activity;
import com.lecturboxd.entity.ActivityType;
import com.lecturboxd.entity.Follow;
import com.lecturboxd.entity.Lecture;
import com.lecturboxd.entity.Review;
import com.lecturboxd.entity.User;
import com.lecturboxd.repository.ActivityRepository;
import com.lecturboxd.repository.FollowRepository;
import com.lecturboxd.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private FeedMapper feedMapper;

    @InjectMocks
    private FeedService feedService;

    @Test
    void getFeedIncludesSelfAndFollowedUsers() {
        UUID me = UUID.randomUUID();
        UUID followedId = UUID.randomUUID();

        User followed = new User();
        followed.setId(followedId);

        Follow follow = new Follow();
        follow.setFollowed(followed);

        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(me)).thenReturn(List.of(follow));
        when(activityRepository.findByUserIdIn(any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Pageable pageable = PageRequest.of(0, 20);
        Page<FeedItemResponse> page = feedService.getFeedForUser(me, pageable);

        assertTrue(page.isEmpty());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<UUID>> idsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(activityRepository).findByUserIdIn(idsCaptor.capture(), eq(pageable));
        assertTrue(idsCaptor.getValue().contains(me));
        assertTrue(idsCaptor.getValue().contains(followedId));
    }

    @Test
    void getFeedMapsReviewActivityWithLinkedReview() {
        UUID me = UUID.randomUUID();
        User actor = new User();
        actor.setId(me);
        actor.setName("Mate");

        Lecture lecture = new Lecture();
        lecture.setId(50L);
        lecture.setTitle("Intro");

        Review review = new Review();
        review.setId(6L);
        review.setRating(5);
        review.setComment("great");
        review.setUser(actor);
        review.setLecture(lecture);

        Activity activity = new Activity();
        activity.setId(1L);
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setUser(actor);
        activity.setLecture(lecture);
        activity.setReview(review);
        activity.setCreatedAt(LocalDateTime.now());

        FeedItemResponse mapped = new FeedItemResponse(
                1L, ActivityType.REVIEW_CREATED, me, "Mate", 50L, "Intro",
                6L, null, 5, "great", activity.getCreatedAt()
        );

        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(me)).thenReturn(List.of());
        when(activityRepository.findByUserIdIn(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(activity)));
        when(feedMapper.toResponse(activity, review)).thenReturn(mapped);

        Page<FeedItemResponse> page = feedService.getFeedForUser(me, PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertEquals("great", page.getContent().get(0).getComment());
        assertEquals(5, page.getContent().get(0).getRating());
        verify(feedMapper).toResponse(activity, review);
    }

    @Test
    void getFeedFallsBackToReviewLookupWhenActivityHasNoReview() {
        UUID me = UUID.randomUUID();
        User actor = new User();
        actor.setId(me);

        Lecture lecture = new Lecture();
        lecture.setId(50L);

        Review review = new Review();
        review.setId(9L);
        review.setRating(4);
        review.setComment("fallback");

        Activity activity = new Activity();
        activity.setId(2L);
        activity.setType(ActivityType.REVIEW_CREATED);
        activity.setUser(actor);
        activity.setLecture(lecture);
        activity.setReview(null);

        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(me)).thenReturn(List.of());
        when(activityRepository.findByUserIdIn(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(activity)));
        when(reviewRepository.findByUserIdAndLectureId(me, 50L)).thenReturn(Optional.of(review));
        when(feedMapper.toResponse(activity, review)).thenReturn(new FeedItemResponse());

        feedService.getFeedForUser(me, PageRequest.of(0, 10));

        verify(reviewRepository).findByUserIdAndLectureId(me, 50L);
        verify(feedMapper).toResponse(activity, review);
    }
}
