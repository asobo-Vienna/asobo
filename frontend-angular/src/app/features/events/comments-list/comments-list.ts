import {Component, inject, input, output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Comment} from '../models/comment';
import {DatePipe} from '@angular/common';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {RouterLink} from '@angular/router';
import {Textarea} from 'primeng/textarea';
import {AuthService} from '../../auth/services/auth-service';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {User} from '../../auth/models/user';
import {Event} from '../models/event';


@Component({
  selector: 'app-comments-list',
  imports: [
    FormsModule,
    DatePipe,
    RouterLink,
    Textarea
  ],
  templateUrl: './comments-list.html',
  styleUrl: './comments-list.scss'
})
export class CommentsList {
  authService = inject(AuthService);
  accessControlService = inject(AccessControlService);

  comments = input<List<Comment>>(new List());
  event = input<Event | null>(null);
  currentUser = input<User | null>(null);

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
    this.commentEdited.emit({ ...comment, text });
    this.editingCommentId.set(null);
  }

  cancelEdit() {
    this.editingCommentId.set(null);
    this.editingCommentText.set('');
  }
}
