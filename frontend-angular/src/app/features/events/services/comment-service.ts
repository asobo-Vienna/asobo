import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Comment} from '../models/comment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {List} from '../../../core/data_structures/lists/list';
import {NewComment} from '../models/new-comment';
import {PageResponse} from '../../../shared/entities/page-response';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private http = inject(HttpClient);

  getAllByEventId(eventId: string): Observable<PageResponse<Comment>> {

    let params = new HttpParams();
    params.set('page', 0)
          .set('size', environment.commentDefaultPageSize);

    return this.http
      .get<PageResponse<Comment>>(`${this.getCommentsUrl(eventId)}/paginated`, { params });
  }


  create(comment: NewComment): Observable<Comment> {
    return this.http.post<Comment>(this.getCommentsUrl(comment.eventId), comment);
  }


  delete(comment: Comment): Observable<Comment> {
    return this.http.delete<Comment>(`${this.getCommentsUrl(comment.eventId)}/${comment.id}`);
  }


  edit(updatedComment: Comment): Observable<Comment> {
    return this.http.put<Comment>(
      `${this.getCommentsUrl(updatedComment.eventId)}/${updatedComment.id}`,
      updatedComment
    );
  }


  private getCommentsUrl(eventId: string): string {
    return `${environment.eventsEndpoint}/${eventId}/comments`;
  }
}

