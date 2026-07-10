// TypeScript interfaces and types for activity feed items and social timeline events.

export interface FeedItem {
  id: number;
  type: 'REVIEW_CREATED' | 'LECTURE_LOGGED';
  user: {
    id: string;
    name: string;
  };
  lecture: {
    id: number;
    title: string;
  };
  message: string;
  createdAt: string;
}


