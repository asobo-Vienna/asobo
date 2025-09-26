import {Component, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommentService} from '../comment-service';
import {Comment} from '../models/comment';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-comments-list',
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './comments-list.html',
  styleUrl: './comments-list.scss'
})
export class CommentsList {
  @Input() eventId!: string;
  comments: Comment[] = [];

  constructor(private commentService: CommentService) {}

  ngOnInit(): void {
    this.getAllComments(this.eventId);
  }

  getAllComments(eventId: string): void {
    this.commentService.getAll(eventId).subscribe({
      next: (comments: Comment[]) => { this.comments = comments
        console.log(comments);
      },
      error: (err) => console.error('Error fetching comments:', err)
    })
  }
}
