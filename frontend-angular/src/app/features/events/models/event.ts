import {Participant} from './participant';
import {Comment} from './comment';
import {List} from '../../../core/data_structures/lists/list';

export interface Event {
  id: string;
  title: string;
  pictureURI: string;
  date: string;
  time: string;
  location: string;
  description: string;
  participants: List<Participant>;
  comments: List<Comment>;
}
