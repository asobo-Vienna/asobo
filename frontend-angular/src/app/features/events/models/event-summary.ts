import {EventCoreInfo} from './event-core-info';
import {List} from '../../../core/data-structures/lists/list';
import {User} from '../../auth/models/user';

export interface EventSummary extends EventCoreInfo {
  participantCount: number;
  commentCount: number;
  mediaCount: number;
  eventAdminIds: List<string>;
  creator?: User;
}
