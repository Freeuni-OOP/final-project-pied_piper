package com.lecturboxd.dto.response;

/**
 * EN: Aggregated rating statistics for a lecture (average and total review count).
 * KA: ლექციის აგრეგირებული რეიტინგის სტატისტიკა (საშუალო და მიმოხილვების საერთო რაოდენობა).
 */
public class RatingSummaryResponse {

    private Long lectureId;
    private Double averageRating;
    private Long totalReviews;

    public RatingSummaryResponse() {
    }

    public RatingSummaryResponse(Long lectureId, Double averageRating, Long totalReviews) {
        this.lectureId = lectureId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public Long getLectureId() {
        return lectureId;
    }

    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}
