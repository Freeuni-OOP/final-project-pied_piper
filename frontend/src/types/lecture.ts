export interface LectureLog {
  id: number;
  userId: string;
  lectureId: number;
  lectureTitle: string;
  watchedAt?: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
