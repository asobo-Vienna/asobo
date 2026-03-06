import {List} from '../../../core/data-structures/lists/list';

export interface UserFilters {
  query?: string;
  username?: string;
  email?: string;
  firstName?: string;
  surname?: string;
  location?: string;
  country?: string;
  isActive?: boolean;
  roleIds?: List<number>
}
