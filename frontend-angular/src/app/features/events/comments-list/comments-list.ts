import {Component, inject, input, output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Comment} from '../models/comment';
import {AsyncPipe, DatePipe} from '@angular/common';
import {List} from '../../../core/data-structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {RouterLink} from '@angular/router';
import {Textarea} from 'primeng/textarea';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {Event} from '../models/event';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';


@Component({
  selector: 'app-comments-list',
  imports: [
    FormsModule,
    DatePipe,
    RouterLink,
    Textarea,
    SecureImagePipe,
    AsyncPipe
  ],
  templateUrl: './comments-list.html',
  styleUrl: './comments-list.scss'
})
export class CommentsList {
  accessControlService = inject(AccessControlService);

  comments = input<List<Comment>>(new List());
  event = input<Event | null>(null);

  commentDeleted = output<Comment>();
  commentEdited = output<Comment>();

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;

  editingCommentId = signal<string | null>(null);
  editingCommentText = signal<string>('');

  startEdit(comment: Comment) {
    this.editingCommentId.set(comment.id);
    this.editingCommentText.set(comment.text);
  }

  saveEdit(comment: Comment, text: string) {
    this.commentEdited.emit({...comment, text});
    this.editingCommentId.set(null);
  }

  cancelEdit() {
    this.editingCommentId.set(null);
    this.editingCommentText.set('');
  }
}
