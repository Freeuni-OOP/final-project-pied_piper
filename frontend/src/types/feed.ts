export interface FeedItem {
  id: number;
  type: 'REVIEW_CREATED' | 'LECTURE_LOGGED';
  actorId: string;
  actorName: string;
  lectureId: number;
  lectureTitle: string;
  reviewId?: number | null;
  lectureLogId?: number | null;
  createdAt: string;
}
