import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Comment} from '../../events/models/comment'
import {User} from '../../auth/models/user';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';
import {MediaItem} from '../../events/models/media-item';
import {PageResponse} from '../../../shared/entities/PageResponse';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  public getAllUsers(page: number, size: number): Observable<PageResponse<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<User>>(`${environment.apiBaseUrl}/admin/users`, { params });
  }

  public getAllCommentsWithEventTitle(): Observable<CommentWithEventTitle[]> {
    return this.http.get<CommentWithEventTitle[]>(`${environment.apiBaseUrl}/admin/comments`);
  }

  public getAllMedia(): Observable<MediaItem[]> {
    return this.http.get<MediaItem[]>(`${environment.apiBaseUrl}/admin/media`);
  }

}
