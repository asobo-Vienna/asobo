import {List} from '../../../../core/data_structures/lists/list';

export interface UserFilters {
  firstName?: string;
  surname?: string;
  location?: string;
  country?: string;
  isActive?: boolean;
  roleIds?: List<number>
}
