import {Participant} from './participant';
import {Comment} from './comment';
import {List} from '../../../core/data_structures/lists/list';
import {EventCoreInfo} from './event-core-info';

export interface Event extends EventCoreInfo {
  participants: List<Participant>;
  comments: List<Comment>;
}
