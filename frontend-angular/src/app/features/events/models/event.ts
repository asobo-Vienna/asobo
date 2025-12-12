import {Participant} from './participant';
import {Comment} from './comment';
import {List} from '../../../core/data_structures/lists/list';
import {EventBaseInfo} from './event-base-info';

export interface Event extends EventBaseInfo {
  participants: List<Participant>;
  comments: List<Comment>;
}
