package com.lecturboxd.controller;

import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.dto.response.FeedItemResponse;
import com.lecturboxd.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EN: Social feed API — activity stream for the authenticated user and people they follow.
 * KA: სოციალური ფიდის API — აქტივობების ნაკადი ავთენტიფიცირებული მომხმარებლისა და მისი გამოწერილებისთვის.
 */
@RestController
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    /**
     * EN: GET /api/feed — returns a paginated activity feed for the current user (JWT required).
     * KA: GET /api/feed — აბრუნებს გვერდებად დაყოფილ აქტივობების ფიდს მიმდინარე მომხმარებლისთვის (საჭიროა JWT).
     */
    @GetMapping("/api/feed")
    public Page<FeedItemResponse> getFeed(
            @AuthenticationPrincipal LecturboxdUserPrincipal principal,
            Pageable pageable
    ) {
        return feedService.getFeedForUser(principal.getId(), pageable);
    }
}
