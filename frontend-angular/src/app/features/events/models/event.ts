import {Participant} from './participant';
import {Comment} from './comment';
import {List} from '../../../core/data-structures/lists/list';
import {EventCoreInfo} from './event-core-info';
import {User} from '../../auth/models/user';

export interface Event extends EventCoreInfo {
  creator: User;
  participants: List<Participant>;
  comments: List<Comment>;
  eventAdmins: List<User>;
}
