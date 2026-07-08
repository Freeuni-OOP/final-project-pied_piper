import client from './axiosClient';

export interface Review {
  id: number;
  lectureId: number;
  authorId: string;
  rating: number;
  content: string;
  createdAt: string;
}

export interface RatingSummary {
  average: number;
  count: number;
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

export async function createReview(
  lectureId: number,
  payload: { rating: number; content: string }
): Promise<Review> {
  const res = await client.post(`/api/lectures/${lectureId}/reviews`, payload);
  return res.data;
}

export async function getRatingSummary(lectureId: number): Promise<RatingSummary> {
  const res = await client.get(`/api/lectures/${lectureId}/rating-summary`);
  return res.data;
}
