import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Comment} from '../../events/models/comment'
import {User} from '../../auth/models/user';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiBaseUrl}/admin/users`);
  }

  public getAllCommentsWithEventTitle(): Observable<CommentWithEventTitle[]> {
    return this.http.get<CommentWithEventTitle[]>(`${environment.apiBaseUrl}/admin/comments`);
  }

}
