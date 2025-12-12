import {Component, inject, input, output, signal} from '@angular/core';
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

  author = input<User | null>(null);
  commentCreated = output<Comment>();
  text = signal<string>('');

  onTextAreaLeave(event: Event): void {
    const value: string = (event.target as HTMLTextAreaElement).value;
    this.text.set(value);
  }

  async submit(): Promise<void> {
    const eventId: string | null = this.route.snapshot.paramMap.get('id');
    if (!eventId || !this.text().trim() || !this.author())
      return;

    this.commentService.create({
      text: this.text().trim(),
      authorId: this.author()!.id,
      eventId: eventId
    }).subscribe({
      next: (newComment: Comment) => {
        this.commentCreated.emit(newComment);
        this.text.set('');
      },
      error: (err: Error) => {
        console.error('Error posting comment', err);
      }
    });
  }
}
