import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {Comment} from '../events/models/comment'
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) {}


  getAll(eventId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${environment.eventsAddress}/${eventId}/comments`);
  }

  async createComment(comment: Comment): Promise<void> {
    if (!comment.text) {
      return;
    }

    const createdComment = {
      'text': comment.text.trim(),
      'authorId': comment.authorId,
      'eventId': comment.eventId
    };

    const url: string = `${environment.eventsAddress}/${comment.eventId}/comments`;

    this.http.post<Comment>(url, createdComment).subscribe({
      next: (newComment) => {
        console.log(newComment);
      },
      error: (error) => {
        console.log('Error posting comment: ', error);
      }
    });
  }
}

