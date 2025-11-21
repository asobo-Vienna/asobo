import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Comment} from '../../events/models/comment'

@Injectable({
  providedIn: 'root',
})
export class AdminCommentService {
  private http = inject(HttpClient);

  public getAllComments(): Observable<Comment[]> {
    return this.http.get<Comment[]>(environment.apiBaseUrl + environment.adminSectionBaseUrl + '/comments');
  }
}
