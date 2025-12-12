import {EventBaseInfo} from './event-base-info';

export interface EventSummary extends EventBaseInfo {
  participantCount: number;
  commentCount: number;
  mediaCount: number;
}
