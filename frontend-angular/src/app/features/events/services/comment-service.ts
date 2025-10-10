import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Comment} from '../models/comment'
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {List} from '../../../core/data_structures/lists/list';
import {CreateComment} from '../models/create-comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) {}


  getAllByEventId(eventId: string): Observable<List<Comment>> {
    return this.http
      .get<Comment[]>(`${environment.eventsAddress}/${eventId}/comments`)
      .pipe(map(comments => new List<Comment>(comments)));
  }


  create(comment: CreateComment): Observable<Comment> {
    return this.http.post<Comment>(`${environment.eventsAddress}/${comment.eventId}/comments`, comment);
  }


  delete(comment: Comment): Observable<Comment> {
    return this.http.delete<Comment>(`${environment.eventsAddress}/${comment.eventId}/comments/${comment.id}`);
  }


  edit(comment: Comment): Observable<Comment> {
    console.log('comment to edit ', comment);
    return new Observable<Comment>();
  }
}

