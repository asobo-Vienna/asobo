import {Participant} from './participant';
import {Comment} from './comment';

export interface Event {
  id: string;
  title: string;
  pictureURI: string;
  date: string;
  time: string;
  location: string;
  description: string;
  participants: Participant[];
  comments: Comment[];
}
