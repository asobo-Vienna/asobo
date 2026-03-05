import {Component, inject, OnInit, signal} from '@angular/core';
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {MediaUtilService} from '../../../shared/utils/media/media-util-service';
import {AdminService} from '../services/admin-service';
import {MediaService} from '../../events/services/media-service';
import {ToastService} from '../../../shared/services/toast-service';
import {RouterLink} from '@angular/router';
import {MediaItemWithEventTitle} from '../../../shared/entities/media/media-item-with-event-title';
import {MediumFilters} from '../../../shared/entities/filters/medium-filters';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-admin-media-list',
  imports: [
    PrimeTemplate,
    TableModule,
    RouterLink,
    Spinner,
    SecureImagePipe,
    AsyncPipe
  ],
  templateUrl: './admin-media-list.html',
  styleUrl: './admin-media-list.scss',
})
export class AdminMediaList implements OnInit {
  private adminService = inject(AdminService);
  private mediaService = inject(MediaService);
  private toastService = inject(ToastService);
  viewMode = signal<'table' | 'card'>('table');
  mediaItems = signal<MediaItemWithEventTitle[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);
  mediumFilters = signal<MediumFilters>({});

  private pageCache = new Map<string, MediaItemWithEventTitle[]>();

  ngOnInit(): void {
    this.loadMedia(0, environment.defaultPageSize);
  }

  loadMedia(page: number, size: number): void {
    const cacheKey = `${page}-${size}`;

    if (this.pageCache.has(cacheKey)) {
      this.mediaItems.set(this.pageCache.get(cacheKey)!);
      return;
    }

    this.loading.set(true);

    this.adminService.getAllMediaWithEventTitle(page, size, this.mediumFilters()).subscribe({
      next: (response) => {
        this.pageCache.set(cacheKey, response.content);

        this.mediaItems.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching media:', err);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadMedia(page, event.rows);
  }

  // Clear cache when data changes (after edit/delete)
  clearCache(): void {
    this.pageCache.clear();
  }

  onDelete(mediaItem: MediaItemWithEventTitle): void {
    this.mediaService.delete(mediaItem.eventId, mediaItem).subscribe({
      next: () => {
        this.mediaItems.update(items => items.filter(i => i.id !== mediaItem.id));
        this.totalRecords.update(total => total - 1);
        this.clearCache();
        this.toastService.success('Media item deleted successfully');
      },
      error: (err) => {
        console.error('Error deleting media item:', err);
        this.toastService.error('Failed to delete media item');
      }
    });
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly environment = environment;
  protected readonly UrlUtilService = UrlUtilService;
  protected readonly MediaUtilService = MediaUtilService;
}
