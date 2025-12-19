import {EventCoreInfo} from './event-core-info';

export interface EventSummary extends EventCoreInfo {
  participantCount: number;
  commentCount: number;
  mediaCount: number;
}
