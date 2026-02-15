import {inject, Injectable, signal} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../../auth/models/user';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';
import {PageResponse} from '../../../shared/entities/page-response';
import {MediaItemWithEventTitle} from '../../events/models/media-item-with-event-title';
import {Role} from '../../../shared/entities/role';
import {UserRoles} from '../../../shared/entities/user-roles';
import {UserFilters} from '../../users/user-profile/models/user-filters';
import {CommentFilters} from '../../events/models/comment-filters';
import {EntityFilterService} from './entity-filter-service';
import {MediumFilters} from '../../events/models/medium-filters';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);
  private entityFilterService = inject(EntityFilterService)

  public getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.apiBaseUrl}/roles`);
  }

  public updateUserRoles(userId: string, roles: Role[]): Observable<UserRoles> {
    return this.http.patch<UserRoles>(`${environment.apiBaseUrl}/roles/assign`, {
      userId,
      roles
    });
  }

  public getAllUsers(page: number, size: number, userFilters?: UserFilters): Observable<PageResponse<User>> {
    let params: HttpParams = userFilters
      ? this.entityFilterService.filtersToHttpParams(userFilters)
      : new HttpParams();

    params = params
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<User>>(`${environment.apiBaseUrl}/admin/users/paginated`, { params });
  }



  public getAllCommentsWithEventTitle(page: number, size: number, commentFilters?: CommentFilters): Observable<PageResponse<CommentWithEventTitle>> {
    let params = commentFilters ? this.entityFilterService.filtersToHttpParams(commentFilters) : new HttpParams();

    params = params
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<CommentWithEventTitle>>(`${environment.apiBaseUrl}/admin/comments`, { params });
  }

  public getAllMediaWithEventTitle(page: number, size: number, mediumFilters?: MediumFilters): Observable<PageResponse<MediaItemWithEventTitle>> {
    let params = mediumFilters ? this.entityFilterService.filtersToHttpParams(mediumFilters) : new HttpParams()

      params = params
        .set('page', page.toString())
        .set('size', size.toString());

    return this.http.get<PageResponse<MediaItemWithEventTitle>>(`${environment.apiBaseUrl}/admin/media`, { params});
  }

  public deleteUserById(userId: string): Observable<User> {
    return this.http.delete<User>(`${environment.apiBaseUrl}/users/${userId}`);
  }
}
