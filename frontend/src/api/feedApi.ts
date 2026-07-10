// HTTP client functions for fetching the social activity feed of followed users.

import client from './axiosClient';
import { FeedItem } from '../types/feed';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/**
 * Get paginated activity feed for the current user (activities from followed users)
 */
export async function getFeed(
  page = 0,
  size = 20
): Promise<PageResponse<FeedItem>> {
  const res = await client.get('/api/feed', {
    params: { page, size },
  });
  return res.data;
}


