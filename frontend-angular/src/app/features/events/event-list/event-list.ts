import {Component, computed, inject, input, OnInit, signal} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {AuthService} from '../../auth/services/auth-service';
import {List} from '../../../core/data-structures/lists/list';
import {EventSummary} from '../models/event-summary';
import {routes} from '../../../app.routes';
import {Router} from '@angular/router';
import {EventFilters} from '../models/event-filters';
import {GlobalSearch} from '../../search/global-search/global-search';
import {debounceTime, Subject} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {ToastService} from '../../../shared/services/toast-service';

type SortField = 'date' | 'title' | 'location' | 'isPrivateEvent';

@Component({
  selector: 'app-event-list',
  imports: [
    EventCard,
    GlobalSearch,
    Spinner,
  ],
  templateUrl: './event-list.html',
  styleUrl: './event-list.scss'
})
export class EventList implements OnInit {
  private eventService = inject(EventService);
  private toastService = inject(ToastService);
  authService = inject(AuthService);
  router = inject(Router);

  inputEvents = input<List<EventSummary>>();
  private fetchedEvents = signal<List<EventSummary>>(new List<EventSummary>());
  eventFilters = signal<EventFilters>({});
  searchQuery = signal<string>('');
  loading = signal<boolean>(true);

  // default sort order: ascending by date
  sortField = signal<SortField>('date');
  sortDirection = signal<'asc' | 'desc'>('asc');

  private searchSubject = new Subject<string>();

  hasInputEvents = computed(() => {
    const input = this.inputEvents();
    return !!input && input.size() > 0;
  });

  // Computed: use input if provided, otherwise use fetched
  private sourceEvents = computed(() => {
    const sourceList = this.hasInputEvents()
      ? this.inputEvents()!
      : this.fetchedEvents();

    return new List<EventSummary>([...sourceList.toArray()]);
  });

  // Computed: filtered events (client-side filtering for inputEvents)
  private filteredEvents = computed(() => {
    const source = this.sourceEvents();
    const query = this.searchQuery().toLowerCase().trim();

    if (!query) {
      return source;
    }

    // Client-side filtering when using inputEvents
    if (this.hasInputEvents()) {
      const filtered = source.toArray().filter(event =>
        event.title?.toLowerCase().includes(query) ||
        event.description?.toLowerCase().includes(query) ||
        event.location?.toLowerCase().includes(query)
      );
      return new List<EventSummary>(filtered);
    }

    // For fetched events, filtering happens server-side
    return source;
  });

  events = computed(() => {
    const list = this.filteredEvents();
    const sorted = [...list.toArray()];

    const field = this.sortField();
    const direction = this.sortDirection();

    sorted.sort((a, b) => {
      let comparison = 0;

      if (field === 'isPrivateEvent') {
        const privacyComparison = (a.isPrivateEvent === b.isPrivateEvent) ? 0 : a.isPrivateEvent ? 1 : -1;

        if (privacyComparison !== 0) {
          return direction === 'asc' ? privacyComparison : -privacyComparison;
        }

        comparison = new Date(a.date).getTime() - new Date(b.date).getTime();
        return comparison;
      }

      switch (field) {
        case 'date':
          comparison = new Date(a.date).getTime() - new Date(b.date).getTime();
          break;
        case 'title':
          comparison = (a.title || '').localeCompare(b.title || '');
          break;
        case 'location':
          comparison = (a.location || '').localeCompare(b.location || '');
          break;
      }

      return direction === 'asc' ? comparison : -comparison;
    });

    return new List<EventSummary>(sorted);
  });

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(environment.defaultSearchDebounceTime),
    ).subscribe((query) => {
      this.searchQuery.set(query);
      // Only fetch if we're using fetched events (not input events)
      if (!this.hasInputEvents()) {
        this.fetchEvents();
      }
    });

    // Only fetch if no input was provided
    if (!this.hasInputEvents()) {
      this.fetchEvents();
    }
  }

  private fetchEvents(): void {
    const filters = {...this.eventFilters()};

    if (this.searchQuery()) {
      filters.query = this.searchQuery();
    }

    if (!this.authService.isLoggedIn()) {
      filters.isPrivateEvent = false;
    }

    const params = {
      page: 0,
      size: 100,
      sort: `${this.sortField()},${this.sortDirection()}`
    };

    console.log('Fetching with filters:', filters, 'params:', params);

    this.eventService.getAllEventsPaginated(params, filters).subscribe({
      next: (events) => {
        console.log('Events fetched:', events);
        this.fetchedEvents.set(new List<EventSummary>(events.content));
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching events:', err);
        this.loading.set(false);
      }
    });
  }

  onSort(field: SortField): void {
    if (field === this.sortField()) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

    // Only fetch if we're using fetched events (not input events)
    // For input events, sorting happens client-side in the computed
    if (!this.hasInputEvents()) {
      this.fetchEvents();
    }
  }

  onSearch(query: string): void {
    this.searchSubject.next(query);
  }

  getSortIcon(field: SortField): string {
    if (field !== this.sortField()) return '';
    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }

  public deleteEvent(item: EventSummary) {
    this.events().remove(item); // remove immediately
    this.eventService.deleteEvent(item.id).subscribe({
      error: (err) => {
        console.log(err);
        this.toastService.error('Failed to delete event!');
        this.events().add(item); // revert if backend fails
      }
    });
  }

  protected readonly routes = routes;
}
