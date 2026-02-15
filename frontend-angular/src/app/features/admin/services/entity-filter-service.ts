import {Injectable} from '@angular/core';
import {UserFilters} from '../../users/user-profile/models/user-filters';
import {HttpParams} from '@angular/common/http';
import {CommentFilters} from '../../events/models/comment-filters';

@Injectable({
  providedIn: 'root',
})
export class EntityFilterService {
  filtersToHttpParams<T extends object>(filters: T): HttpParams {
    let params = new HttpParams();

    Object.keys(filters).forEach(key => {
      const value = filters[key as keyof T];
      if (value !== null && value !== undefined && value !== '') {
        if (value instanceof Date) {
          params = params.set(key, value.toISOString());
        } else {
          params = params.set(key, String(value));
        }
      }
    });

    return params;
  }
}
