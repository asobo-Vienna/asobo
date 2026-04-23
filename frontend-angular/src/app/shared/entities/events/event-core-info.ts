import {EventCategory} from './event-category';
import {List} from '../../../core/data-structures/lists/list';

export interface EventCoreInfo {
  id: string;
  title: string;
  pictureURI: string | null;
  date: string;
  time: string;
  location: string;
  categories: List<EventCategory>;
  description: string;
  isPrivateEvent: boolean;
}
