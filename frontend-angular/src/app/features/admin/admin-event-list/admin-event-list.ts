import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {fromEvent} from 'rxjs';
import {Event} from '../../events/models/event';
import {environment} from '../../../../environments/environment';
import {Router, RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {EventService} from '../../events/services/event-service';
import {TableModule} from 'primeng/table';
import {AsyncPipe, DatePipe} from '@angular/common';
import {Tag} from 'primeng/tag';
import {Dialog} from 'primeng/dialog';
import {Button} from 'primeng/button';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {FormsModule} from '@angular/forms';
import {EventSummary} from '../../events/models/event-summary';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {getTextPreview} from '../../../shared/utils/text/text-utils';
import {ToastService} from '../../../shared/services/toast-service';

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
    AsyncPipe,
    ToggleSwitch,
    FormsModule
  ],
  templateUrl: './admin-event-list.html',
  styleUrl: './admin-event-list.scss',
})
export class AdminEventList implements OnInit {
  private eventService = inject(EventService);
  private router = inject(Router);
  private toastService = inject(ToastService);
  private destroyRef = inject(DestroyRef);
  viewMode = signal<'table' | 'card'>('table');
  isMobile = signal(window.innerWidth <= 768);
  events = signal<EventSummary[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);

  showDescriptionDialog = false;
  selectedEvent: Event | null = null;

  private pageCache = new Map<string, EventSummary[]>();

  ngOnInit(): void {
    this.loadEvents(0, environment.defaultPageSize);
    fromEvent(window, 'resize')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.isMobile.set(window.innerWidth <= 768));
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

  onToggleVisibility(event: EventSummary): void {
    this.eventService.updateEvent(event.id, { isPrivateEvent: !event.isPrivateEvent }).subscribe({
      next: () => {
        this.events.update(events =>
          events.map(e => e.id === event.id ? { ...e, isPrivateEvent: !event.isPrivateEvent } : e)
        );
        this.clearCache();
      },
      error: (err) => console.error('Error updating event visibility:', err)
    });
  }

  onEdit(event: Event) {
    this.router.navigate(['/events', event.id], { queryParams: { edit: true } });
  }

  onDelete(event: Event) {
    this.eventService.deleteEvent(event.id).subscribe({
      next: () => {
        this.events.update(events => events.filter(e => e.id !== event.id));
        this.totalRecords.update(total => total - 1);
        this.clearCache();
        this.toastService.success(`Event "${event.title}" deleted successfully`);
      },
      error: (err) => {
        console.error('Error deleting event:', err);
        this.toastService.error(`Failed to delete event "${event.title}"`);
      }
    });
  }

  viewFullDescription(event: Event): void {
    this.selectedEvent = event;
    this.showDescriptionDialog = true;
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  shouldShowViewMore(description: string): boolean {
    return description.length > environment.eventDescriptionPreviewLength && !this.isMobile();
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
  protected readonly getEventDescriptionPreview = getTextPreview;
  protected readonly getTextPreview = getTextPreview;
}
