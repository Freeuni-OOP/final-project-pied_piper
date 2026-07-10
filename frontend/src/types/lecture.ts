// TypeScript interfaces and types for lecture entities, syllabus metadata, and search filters.

export interface LectureLog {
  id: number;
  userId: string;
  lectureId: number;
  lectureName: string;
  notes?: string;
  loggedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}


