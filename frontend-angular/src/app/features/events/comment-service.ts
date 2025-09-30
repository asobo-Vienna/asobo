import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {Comment} from '../events/models/comment'
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private baseUrl = environment.eventsAddress;

  constructor(private http: HttpClient) {}


  getAllByEventId(eventId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/${eventId}/comments`);
  }


  create(comment: Comment): Observable<Comment> {
    const createdComment = {
      'text': comment.text.trim(),
      'authorId': comment.authorId,
      'eventId': comment.eventId
    };

    return this.http.post<Comment>(`${this.baseUrl}/${comment.eventId}/comments`, createdComment);
  }


  delete(comment: Comment): Observable<Comment> {
    return this.http.delete<Comment>(`${this.baseUrl}/${comment.eventId}/comments/${comment.id}`);
  }


  edit(comment: Comment): Observable<Comment> {
    console.log('comment to edit ', comment);
    return new Observable<Comment>();
  }
}

