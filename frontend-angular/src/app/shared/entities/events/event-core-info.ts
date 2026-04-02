import {EventCategory} from '../../enums/event-category';

export interface EventCoreInfo {
  id: string;
  title: string;
  pictureURI: string | null;
  date: string;
  time: string;
  location: string;
  category: EventCategory;
  description: string;
  isPrivateEvent: boolean;
}
