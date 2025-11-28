import {Component, inject, OnInit, signal} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { DatePipe } from '@angular/common';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {AdminService} from '../services/admin-service';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';
import { RouterLink } from '@angular/router';

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
  private adminService = inject(AdminService);
  comments = signal<CommentWithEventTitle[]>([]);
  loading = true;

  ngOnInit(): void {
    this.adminService.getAllCommentsWithEventTitle().subscribe({
      next: (comments) => {
        this.comments.set(comments);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching comments:', err);
        this.loading = false;
      }
    });
    return;
  }

  onEdit(comment: any) {
    console.log('Editing comment:', comment);
  }

  onDelete(comment: any) {
    console.log('Deleting comment:', comment);
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}

