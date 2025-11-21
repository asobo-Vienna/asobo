import {Component, inject, OnInit, signal} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { DatePipe } from '@angular/common';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {AdminCommentService} from '../services/admin-comment-service';
import {Comment} from '../../events/models/comment'

@Component({
  selector: 'app-admin-comment-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
  ],
  templateUrl: './admin-comment-list.html',
  styleUrl: './admin-comment-list.scss',
})
export class AdminCommentList implements OnInit {
  private adminCommentService = inject(AdminCommentService);
  comments = signal<Comment[]>([]);

  ngOnInit(): void {
    this.adminCommentService.getAllComments().subscribe({
      next: (comments) => {
        this.comments.set(comments);
      },
      error: (err) => console.error('Error fetching comments:', err)
    });
    return;
  }

  onEdit(comment: any) {
    console.log('Editing comment:', comment);
  }

  onDelete(comment: any) {
    console.log('Deleting comment:', comment);
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}

