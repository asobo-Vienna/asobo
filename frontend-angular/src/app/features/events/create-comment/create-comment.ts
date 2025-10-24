import {Component, EventEmitter, inject, Input, Output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommentService} from '../services/comment-service';
import {ActivatedRoute} from '@angular/router';
import {Comment} from '../models/comment';
import {User} from '../../auth/models/user';

@Component({
  selector: 'app-new-comment',
  imports: [
    FormsModule
  ],
  templateUrl: './create-comment.html',
  styleUrl: './create-comment.scss'
})
export class CreateComment {
  private commentService = inject(CommentService);
  private route = inject(ActivatedRoute);

  @Input() author!: User | null ;
  @Output() commentCreated: EventEmitter<Comment> = new EventEmitter<Comment>();
  text: string = '';

  async submit(): Promise<void> {
    const eventId: string | null = this.route.snapshot.paramMap.get('id');
    if (!eventId || !this.text.trim() || !this.author)
      return;

    this.commentService.create({
      text: this.text.trim(),
      authorId: this.author.id,
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
