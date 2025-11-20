import {Component, inject, OnInit, signal} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { DatePipe } from '@angular/common';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {RouterLink} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {AdminCommentService} from '../services/admin-comment-service';
import {Comment} from '../../events/models/comment'

@Component({
  selector: 'app-admin-comment-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
    RouterLink
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
        console.log(this.comments());
      },
      error: (err) => console.error('Error fetching comments:', err)
    });
    return;
  }

  protected readonly UrlUtilService = UrlUtilService;

  onEdit(comment: any) {
    console.log('Editing comment:', comment);
  }

  onDelete(comment: any) {
    console.log('Deleting comment:', comment);
  }

  getUserRouterLink(username: string): string {
    return `${environment.userProfileBaseUrl}${username}`;
  }

  protected readonly environment = environment;
}

