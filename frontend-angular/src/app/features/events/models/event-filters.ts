import {List} from '../../../core/data_structures/lists/list';

export interface EventFilters {
  location?: string;
  creatorId?: string;
  date?: Date;
  dateFrom?: Date;
  dateTo?: Date;
  isPrivateEvent?: boolean;
  eventAdminIds?: List<string>;
  participantIds?: List<string>;
}
