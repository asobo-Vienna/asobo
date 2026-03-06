import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../../../shared/entities/users/user';
import {CommentWithEventTitle} from '../../../shared/entities/comments/comment-with-event-title';
import {PageResponse} from '../../../shared/entities/page-response';
import {MediaItemWithEventTitle} from '../../../shared/entities/media/media-item-with-event-title';
import {Role} from '../../../shared/entities/role';
import {UserRoles} from '../../../shared/entities/users/user-roles';
import {UserFilters} from '../../../shared/entities/filters/user-filters';
import {CommentFilters} from '../../../shared/entities/filters/comment-filters';
import {EntityFilterService} from './entity-filter-service';
import {MediumFilters} from '../../../shared/entities/filters/medium-filters';

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

    return this.http.get<PageResponse<User>>(`${environment.apiBaseUrl}/admin/users/paginated`, {params});
  }


  public getAllCommentsWithEventTitle(page: number, size: number, commentFilters?: CommentFilters): Observable<PageResponse<CommentWithEventTitle>> {
    let params = commentFilters ? this.entityFilterService.filtersToHttpParams(commentFilters) : new HttpParams();

    params = params
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<CommentWithEventTitle>>(`${environment.apiBaseUrl}/admin/comments`, {params});
  }

  public getAllMediaWithEventTitle(page: number, size: number, mediumFilters?: MediumFilters): Observable<PageResponse<MediaItemWithEventTitle>> {
    let params = mediumFilters ? this.entityFilterService.filtersToHttpParams(mediumFilters) : new HttpParams()

    params = params
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<MediaItemWithEventTitle>>(`${environment.apiBaseUrl}/admin/media`, {params});
  }

  public deleteUserById(userId: string): Observable<User> {
    return this.http.delete<User>(`${environment.apiBaseUrl}/users/${userId}`);
  }

  public reactivateUserById(userId: string): Observable<User> {
    return this.http.post<User>(`${environment.apiBaseUrl}/users/${userId}`, {});
  }
}
