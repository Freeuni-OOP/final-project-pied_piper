package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.ReviewResponse;
import com.lecturboxd.entity.Review;

public final class ReviewMapper {

    private ReviewMapper() {
    }

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
