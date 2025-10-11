import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Comment} from '../models/comment';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
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
      .pipe(map(comments => new List<Comment>(comments)));
  }


  create(comment: NewComment): Observable<Comment> {
    return this.http.post<Comment>(this.getCommentsUrl(comment.eventId), comment);
  }


  delete(comment: Comment): Observable<Comment> {
    return this.http.delete<Comment>(`${this.getCommentsUrl(comment.eventId)}/${comment.id}`);
  }


  edit(comment: Comment): Observable<Comment> {
    console.log('comment to edit ', comment);
    return new Observable<Comment>();
  }


  private getCommentsUrl(eventId: string): string {
    return `${environment.eventsAddress}/${eventId}/comments`;
  }
}

