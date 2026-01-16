import {inject, Injectable, signal} from '@angular/core';
import {Observable, take} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../../auth/models/user';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';
import {PageResponse} from '../../../shared/entities/page-response';
import {MediaItemWithEventTitle} from '../../events/models/media-item-with-event-title';
import {Role} from '../../../shared/entities/role';
import {UserRoles} from '../../../shared/entities/user-roles';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  public getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.apiBaseUrl}/roles`)
      .pipe(take(1));
  }

  public updateUserRoles(userId: string, roles: Role[]): Observable<UserRoles> {
    return this.http.patch<UserRoles>(`${environment.apiBaseUrl}/roles/assign`, {
      userId,
      roles
    })
      .pipe(take(1));
  }

  public getAllUsers(page: number, size: number): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<User>>(`${environment.apiBaseUrl}/admin/users`, { params })
      .pipe(take(1));
  }

  public getAllCommentsWithEventTitle(page: number, size: number): Observable<PageResponse<CommentWithEventTitle>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<CommentWithEventTitle>>(`${environment.apiBaseUrl}/admin/comments`, { params })
      .pipe(take(1));
  }

  public getAllMediaWithEventTitle(page: number, size: number): Observable<PageResponse<MediaItemWithEventTitle>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<MediaItemWithEventTitle>>(`${environment.apiBaseUrl}/admin/media`, { params })
      .pipe(take(1));
  }

}
