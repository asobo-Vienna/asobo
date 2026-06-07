export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalActiveElements: number;
  totalPages: number;
  size: number;
  number: number;
}
