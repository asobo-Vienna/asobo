import {MediaItem} from './media-item';

export interface MediaItemWithEventTitle extends MediaItem {
  eventId: string;
}
