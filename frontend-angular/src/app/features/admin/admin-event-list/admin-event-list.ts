import {Component, inject, OnInit, signal} from '@angular/core';
import {Event} from '../../events/models/event';
import {environment} from '../../../../environments/environment';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {EventService} from '../../events/services/event-service';
import {TableModule} from 'primeng/table';
import {AsyncPipe, DatePipe} from '@angular/common';
import {Tag} from 'primeng/tag';
import {Dialog} from 'primeng/dialog';
import {Button} from 'primeng/button';
import {EventSummary} from '../../events/models/event-summary';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';

@Component({
  selector: 'app-admin-event-list',
  imports: [
    RouterLink,
    TableModule,
    DatePipe,
    Tag,
    Dialog,
    Button,
    Spinner,
    SecureImagePipe,
    AsyncPipe
  ],
  templateUrl: './admin-event-list.html',
  styleUrl: './admin-event-list.scss',
})
export class AdminEventList implements OnInit {
  private eventService = inject(EventService);
  viewMode = signal<'table' | 'card'>('table');
  events = signal<EventSummary[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);

  showDescriptionDialog = false;
  selectedEvent: Event | null = null;

  private pageCache = new Map<string, EventSummary[]>();

  ngOnInit(): void {
    this.loadEvents(0, environment.defaultPageSize);
  }

  loadEvents(page: number, size: number): void {
    const cacheKey = `${page}-${size}`;

    // Check if page is already cached
    if (this.pageCache.has(cacheKey)) {
      this.events.set(this.pageCache.get(cacheKey)!);
      return;
    }

    this.loading.set(true);

    this.eventService.getAllEventsPaginated({page, size}, {}).subscribe({
      next: (response) => {
        // Cache the page data
        this.pageCache.set(cacheKey, response.content);

        this.events.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching events:', err);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadEvents(page, event.rows);
  }

  // Clear cache when data changes (after edit/delete)
  clearCache(): void {
    this.pageCache.clear();
  }

  onEdit(event: Event) {
    console.log('Editing event:', event);
    this.clearCache();
  }

  onDelete(event: Event) {
    console.log('Deleting event:', event);
    this.clearCache();
  }

  viewFullDescription(event: Event): void {
    this.selectedEvent = event;
    this.showDescriptionDialog = true;
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  getEventDescriptionPreview(description: string): string {
    if (description.length > environment.eventDescriptionPreviewLength) {
      return `${description.substring(0, environment.eventDescriptionPreviewLength - 1)}...`;
    }
    return description;
  }

  shouldShowViewMore(description: string): boolean {
    return description.length > environment.eventDescriptionPreviewLength;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}
