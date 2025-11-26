import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TextareaModule } from 'primeng/textarea';
import { CommentService } from '../services/comment-service';
import { ActivatedRoute } from '@angular/router';
import { Comment } from '../models/comment';

@Component({
  selector: 'app-edit-comment',
  imports: [FormsModule, 
    ButtonModule, 
    TextareaModule],
  templateUrl: './edit-comment.html',
  styleUrl: './edit-comment.scss',
})
export class EditComment {
  private commentService = inject(CommentService);
  private route = inject(ActivatedRoute);

  @Input() comment!: Comment;
  @Output() commentEdited = new EventEmitter<Comment>();
  @Output() cancelEdit = new EventEmitter<void>();
  text = signal('');

  ngOnInit() {
    this.text.set(this.comment.text);
  }

  save() {
    console.log(`Updating comment ${this.comment}`);
  //   this.commentService.update(this.comment.id, { text: this.text() }).subscribe({
  //     next: (updated) => this.commentEdited.emit(updated),
  //   });
  }

  cancel() {
    this.cancelEdit.emit();
  }
}
