import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Comment} from '../models/comment';
import {HttpClient} from '@angular/common/http';
import {map, Observable, take} from 'rxjs';
import {List} from '../../../core/data_structures/lists/list';
import {NewComment} from '../models/new-comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private http = inject(HttpClient);

  getAllByEventId(eventId: string): Observable<List<Comment>> {
    return this.http
      .get<Comment[]>(this.getCommentsUrl(eventId))
      .pipe(take(1))
      .pipe(map(comments => new List<Comment>(comments)));
  }

  create(comment: NewComment): Observable<Comment> {
    return this.http.post<Comment>(this.getCommentsUrl(comment.eventId), comment)
      .pipe(take(1));
  }

  delete(comment: Comment): Observable<Comment> {
    return this.http.delete<Comment>(`${this.getCommentsUrl(comment.eventId)}/${comment.id}`)
      .pipe(take(1));
  }

  edit(updatedComment: Comment): Observable<Comment> {
    return this.http.put<Comment>(
      `${this.getCommentsUrl(updatedComment.eventId)}/${updatedComment.id}`,
      updatedComment
    ).pipe(take(1));
  }

  private getCommentsUrl(eventId: string): string {
    return `${environment.eventsEndpoint}/${eventId}/comments`;
  }
}

