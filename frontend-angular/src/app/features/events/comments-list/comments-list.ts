import {Component, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';
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
  @Input() comments!: Comment[];
}
