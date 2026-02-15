import {Component, inject, OnInit, signal} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { DatePipe } from '@angular/common';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {AdminService} from '../services/admin-service';
import {CommentWithEventTitle} from '../../events/models/comment-with-event-title';
import { RouterLink } from '@angular/router';
import {UserFilters} from '../../users/user-profile/models/user-filters';
import {CommentFilters} from '../../events/models/comment-filters';

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
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);
  commentFilters = signal<CommentFilters>({});

  private pageCache = new Map<string, CommentWithEventTitle[]>();

  ngOnInit(): void {
    this.loadComments(0, environment.defaultPageSize);
  }

  loadComments(page: number, size: number): void {
    const cacheKey = `${page}-${size}`;

    if (this.pageCache.has(cacheKey)) {
      this.comments.set(this.pageCache.get(cacheKey)!);
      return;
    }

    this.loading.set(true);

    this.adminService.getAllCommentsWithEventTitle(page, size, this.commentFilters()).subscribe({
      next: (response) => {
        // Cache the page data
        this.pageCache.set(cacheKey, response.content);

        this.comments.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching comments:', err);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadComments(page, event.rows);
  }

  // Clear cache when data changes (after edit/delete)
  clearCache(): void {
    this.pageCache.clear();
  }

  onEdit(comment: any) {
    console.log('Editing comment:', comment);
    this.clearCache();
  }

  onDelete(comment: any) {
    console.log('Deleting comment:', comment);
    this.clearCache();
    // stay on page, thus track currently loaded comments and update numbers:
    // this.loadComments(0, 10);
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}

