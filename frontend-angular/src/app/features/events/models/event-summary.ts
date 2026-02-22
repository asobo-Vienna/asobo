import {EventCoreInfo} from './event-core-info';
import {User} from '../../auth/models/user';
import {List} from '../../../core/data-structures/lists/list';

export interface EventSummary extends EventCoreInfo {
  participantCount: number;
  commentCount: number;
  mediaCount: number;
  eventAdminIds: List<string>;
  creator?: User;
}
