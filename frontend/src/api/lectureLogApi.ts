// HTTP client functions for lecture logs and user lecture history.

import client from './axiosClient';
import { LectureLog, PageResponse } from '../types/lecture';

/**
 * Log a lecture as attended/watched by the current user
 */
export async function logLecture(
  lectureId: number,
  notes?: string
): Promise<LectureLog> {
  const res = await client.post(`/api/lectures/${lectureId}/logs`, {
    notes: notes || '',
  });
  return res.data;
}

/**
 * Get the current user's log for a lecture, or null if not logged
 */
export async function getMyLectureLog(lectureId: number): Promise<LectureLog | null> {
  try {
    const res = await client.get(`/api/lectures/${lectureId}/logs/me`);
    return res.data;
  } catch (err: any) {
    if (err?.status === 404) return null;
    throw err;
  }
}

/**
 * Remove the current user's log for a lecture
 */
export async function unlogLecture(lectureId: number): Promise<void> {
  await client.delete(`/api/lectures/${lectureId}/logs/me`);
}

/**
 * Get lecture logs for a specific user (paginated)
 */
export async function getUserLectureLogs(
  userId: string,
  page = 0,
  size = 20
): Promise<PageResponse<LectureLog>> {
  const res = await client.get(`/api/users/${userId}/logs`, {
    params: { page, size },
  });
  return res.data;
}

/**
 * Delete a lecture log entry
 */
export async function deleteLectureLog(logId: number): Promise<void> {
  await client.delete(`/api/logs/${logId}`);
}
