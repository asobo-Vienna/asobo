import {Component, inject, OnInit, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {TagModule} from 'primeng/tag';
import {DatePipe} from '@angular/common';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from '../../../../environments/environment';
import {AdminService} from '../services/admin-service';
import {CommentWithEventTitle} from '../../../shared/entities/comments/comment-with-event-title';
import {RouterLink} from '@angular/router';
import {CommentFilters} from '../../../shared/entities/filters/comment-filters';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {CommentService} from '../../events/services/comment-service';
import {ToastService} from '../../../shared/services/toast-service';
import {FormsModule} from '@angular/forms';
import {Textarea} from 'primeng/textarea';
import {ConfirmDialogService} from '../../../shared/services/confirm-dialog-service';

@Component({
  selector: 'app-admin-comment-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
    RouterLink,
    Spinner,
    FormsModule,
    Textarea
  ],
  templateUrl: './admin-comment-list.html',
  styleUrl: './admin-comment-list.scss',
})
export class AdminCommentList implements OnInit {
  private adminService = inject(AdminService);
  private commentService = inject(CommentService);
  private toastService = inject(ToastService);
  private confirmDialogService = inject(ConfirmDialogService);
  viewMode = signal<'table' | 'card'>('table');
  comments = signal<CommentWithEventTitle[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);
  commentFilters = signal<CommentFilters>({});
  editingCommentId = signal<string | null>(null);
  editingCommentText = '';

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

  startEdit(comment: CommentWithEventTitle): void {
    this.editingCommentId.set(comment.id);
    this.editingCommentText = comment.text;
  }

  saveEdit(comment: CommentWithEventTitle): void {
    this.confirmDialogService
      .confirmSave()
      .then(confirmed => {
        if (!confirmed) return;

        this.commentService.edit({...comment, text: this.editingCommentText}).subscribe({
          next: (updated) => {
            this.comments.update(items =>
              items.map(c => c.id === comment.id ? {
                ...c,
                text: updated.text,
                modificationDate: updated.modificationDate
              } : c)
            );
            this.clearCache();
            this.editingCommentId.set(null);
            this.toastService.success('Comment updated successfully');
          },
          error: (err) => {
            console.error('Error updating comment:', err);
            this.toastService.error('Failed to update comment');
          }
        });
      });
  }

  cancelEdit(): void {
    this.editingCommentId.set(null);
    this.editingCommentText = '';
  }

  onDelete(comment: CommentWithEventTitle): void {
    this.confirmDialogService
      .confirmDelete('comment', comment.text)
      .then(confirmed => {
        if (!confirmed) return;

        this.commentService.delete(comment).subscribe({
          next: () => {
            this.comments.update(items => items.filter(c => c.id !== comment.id));
            this.totalRecords.update(total => total - 1);
            this.clearCache();
            this.toastService.success('Comment deleted successfully');
          },
          error: (err) => {
            console.error('Error deleting comment:', err);
            this.toastService.error('Failed to delete comment');
          }
        });
      });
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}

