import client from './axiosClient';

export interface ReviewAuthor {
  id: string;
  name: string;
}

export interface Review {
  id: number;
  rating: number;
  comment: string;
  author: ReviewAuthor;
  lecture?: { id: number; title: string };
  createdAt: string;
  updatedAt?: string;
}

export interface RatingSummary {
  lectureId: number;
  averageRating: number;
  totalReviews: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export async function getReviewsForLecture(
  lectureId: number,
  page = 0,
  size = 20
): Promise<PageResponse<Review>> {
  const res = await client.get(`/api/lectures/${lectureId}/reviews`, {
    params: { page, size },
  });
  return res.data;
}

export async function getReviewsByUser(
  userId: string,
  page = 0,
  size = 20
): Promise<PageResponse<Review>> {
  const res = await client.get(`/api/users/${userId}/reviews`, {
    params: { page, size },
  });
  return res.data;
}

export async function createReview(
  lectureId: number,
  payload: { rating: number; comment: string }
): Promise<Review> {
  const res = await client.post(`/api/lectures/${lectureId}/reviews`, payload);
  return res.data;
}

export async function deleteReview(reviewId: number): Promise<void> {
  await client.delete(`/api/reviews/${reviewId}`);
}

export async function getRatingSummary(lectureId: number): Promise<RatingSummary> {
  const res = await client.get(`/api/lectures/${lectureId}/rating-summary`);
  return res.data;
}
