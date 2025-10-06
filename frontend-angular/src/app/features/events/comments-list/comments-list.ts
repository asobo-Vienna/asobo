import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Comment} from '../models/comment';
import {DatePipe} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-comments-list',
  imports: [
    FormsModule,
    DatePipe,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './comments-list.html',
  styleUrl: './comments-list.scss'
})
export class CommentsList {
  @Input() comments!: List<Comment>;
  @Output() commentDeleted = new EventEmitter<Comment>();
  @Output() commentEdited = new EventEmitter<Comment>();
  protected readonly UrlUtilService = UrlUtilService;
}
