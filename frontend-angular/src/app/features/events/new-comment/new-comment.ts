import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommentService} from '../comment-service';
import {ActivatedRoute} from '@angular/router';
import {Comment} from '../models/comment';

@Component({
  selector: 'app-new-comment',
  imports: [
    FormsModule
  ],
  templateUrl: './new-comment.html',
  styleUrl: './new-comment.scss'
})
export class NewComment {

  @Output() commentCreated: EventEmitter<Comment> = new EventEmitter<Comment>();
  text: string = '';
  // TODO remove hardcoded ID here!!!
  authorId: string = '7da69d8e-55c7-4a96-ac6d-cb207e4e8a21';

  constructor(private commentService: CommentService,
              private route: ActivatedRoute) {
  }

  async submit(): Promise<void> {
    const eventId: string | null = this.route.snapshot.paramMap.get('id');
    if (!eventId || !this.text.trim())
      return;

    this.commentService.createComment({
      text: this.text,
      authorId: this.authorId,
      eventId: eventId
    }).subscribe({
      next: (newComment: Comment) => {
        this.commentCreated.emit(newComment);
        this.text = '';
      },
      error: (err: Error) => {
        console.error('Error posting comment', err);
      }
    });
  }
}
