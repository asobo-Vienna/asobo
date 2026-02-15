import {Component, inject, OnInit, signal} from '@angular/core';
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AdminService} from '../services/admin-service';
import { RouterLink } from '@angular/router';
import {MediaItemWithEventTitle} from '../../events/models/media-item-with-event-title';
import {MediumFilters} from '../../events/models/medium-filters';

@Component({
  selector: 'app-admin-media-list',
    imports: [
        PrimeTemplate,
        TableModule,
        RouterLink
    ],
  templateUrl: './admin-media-list.html',
  styleUrl: './admin-media-list.scss',
})
export class AdminMediaList implements OnInit {
  private adminService = inject(AdminService);
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

  onEdit(mediaItem: any) {
    console.log('Editing media item:', mediaItem);
    this.clearCache();
  }

  onDelete(mediaItems: any) {
    console.log('Deleting media item:', mediaItems);
    this.clearCache();
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly environment = environment;
  protected readonly UrlUtilService = UrlUtilService;
}
