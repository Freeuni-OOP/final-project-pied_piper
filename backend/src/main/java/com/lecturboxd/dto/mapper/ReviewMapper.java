package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Review;

/**
 * EN: Maps Review entities to ReviewResponse DTOs with author and lecture summaries.
 * KA: Review ერთეულებს ReviewResponse DTO-ებად გარდაქმნის ავტორისა და ლექციის შეჯამებით.
 */
public final class ReviewMapper {

    private ReviewMapper() {
    }

    /**
     * EN: Converts a Review entity into a ReviewResponse including nested author and lecture summaries.
     * KA: Review ერთეულს ReviewResponse-ად გარდაქმნის ჩადგმული ავტორისა და ლექციის შეჯამებით.
     */
    public static ReviewResponse toResponse(Review review) {
        ReviewResponse.AuthorSummary author = new ReviewResponse.AuthorSummary(
                review.getUser().getId(),
                review.getUser().getName()
        );
        ReviewResponse.LectureSummary lecture = new ReviewResponse.LectureSummary(
                review.getLecture().getId(),
                review.getLecture().getTitle()
        );

        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                author,
                lecture,
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
