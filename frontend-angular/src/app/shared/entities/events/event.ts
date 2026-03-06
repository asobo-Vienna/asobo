import {Participant} from './participant';
import {Comment} from '../comments/comment';
import {List} from '../../../core/data-structures/lists/list';
import {EventCoreInfo} from './event-core-info';
import {User} from '../users/user';

export interface Event extends EventCoreInfo {
  creator: User;
  participants: List<Participant>;
  comments: List<Comment>;
  eventAdmins: List<User>;
}
