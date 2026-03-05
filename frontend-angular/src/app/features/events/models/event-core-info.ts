import {User} from '../../auth/models/user';

export interface EventCoreInfo {
  id: string;
  title: string;
  pictureURI: string | null;
  date: string;
  time: string;
  location: string;
  description: string;
  isPrivateEvent: boolean;
}
