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

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  public getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.apiBaseUrl}/roles`);
  }

  public updateUserRoles(userId: string, roles: Role[]): Observable<UserRoles> {
    return this.http.patch<UserRoles>(`${environment.apiBaseUrl}/roles/assign`, {
      userId,
      roles
    });
  }

  public getAllUsers(userFilters: UserFilters, page: number, size: number): Observable<PageResponse<User>> {
    let params: HttpParams = this.filtersToHttpParams(userFilters)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<User>>(`${environment.apiBaseUrl}/admin/users/paginated`, { params });
  }

  private filtersToHttpParams(filters: UserFilters): HttpParams {
    let params = new HttpParams();

    Object.keys(filters).forEach(key => {
      const value = filters[key as keyof UserFilters];
      if (value !== null && value !== undefined) {
        params = params.set(key, String(value));
      }
    });

    return params;
  }

  public getAllCommentsWithEventTitle(page: number, size: number): Observable<PageResponse<CommentWithEventTitle>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<CommentWithEventTitle>>(`${environment.apiBaseUrl}/admin/comments`, { params });
  }

  public getAllMediaWithEventTitle(page: number, size: number): Observable<PageResponse<MediaItemWithEventTitle>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<MediaItemWithEventTitle>>(`${environment.apiBaseUrl}/admin/media`, { params});
  }

  public deleteUserById(userId: string): Observable<User> {
    return this.http.delete<User>(`${environment.apiBaseUrl}/users/${userId}`);
  }
}
