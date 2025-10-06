import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Comment} from '../models/comment'
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {List} from '../../../core/data_structures/lists/list';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) {}


  getAllByEventId(eventId: string): Observable<List<Comment>> {
    return this.http
      .get<Comment[]>(this.getCommentsUrl(eventId))
      .pipe(map(comments => new List<Comment>(comments)));
  }


  create(comment: Comment): Observable<Comment> {
    const createdComment = {
      'text': comment.text.trim(),
      'authorId': comment.authorId,
      'eventId': comment.eventId
    };
    return this.http.post<Comment>(this.getCommentsUrl(comment.eventId), createdComment);
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

