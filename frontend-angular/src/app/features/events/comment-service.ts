import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {Comment} from '../events/models/comment'
import {HttpClient} from '@angular/common/http';
import {Observable, Subscription} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) {}


  getAll(eventId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${environment.eventsAddress}/${eventId}/comments`);
  }


  createComment(comment: Comment): Observable<Comment> {
    const createdComment = {
      'text': comment.text.trim(),
      'authorId': comment.authorId,
      'eventId': comment.eventId
    };

    const url: string = `${environment.eventsAddress}/${comment.eventId}/comments`;
    return this.http.post<Comment>(url, createdComment);
  }
}

