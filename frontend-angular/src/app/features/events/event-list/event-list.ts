import {Component, computed, inject, input, OnInit, signal} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {Event} from '../models/event'
import {AuthService} from '../../auth/services/auth-service';
import {List} from '../../../core/data_structures/lists/list';
import {EventSummary} from '../models/event-summary';
import {routes} from '../../../app.routes';
import {Router} from '@angular/router';

type SortField = 'date' | 'title' | 'location' | 'isPrivateEvent';
import {HttpParams} from '@angular/common/http';
import {EventFilters} from '../models/event-filters';
import {GlobalSearch} from '../../search/global-search/global-search';
import {debounceTime, Subject} from 'rxjs';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-event-list',
  imports: [
    EventCard,
    GlobalSearch,
  ],
  templateUrl: './event-list.html',
  styleUrl: './event-list.scss'
})
export class EventList implements OnInit {
  private eventService = inject(EventService);
  authService = inject(AuthService);
  router = inject(Router);

  inputEvents = input<List<EventSummary>>();
  private fetchedEvents = signal<List<EventSummary>>(new List<EventSummary>());
  eventFilters = signal<EventFilters>({});
  searchQuery = signal<string>('');

  // default sort order: ascending by date
  sortField = signal<SortField>('date');
  sortDirection = signal<'asc' | 'desc'>('asc');

  private searchSubject = new Subject<string>();

  hasInputEvents = computed(() => {
    const input = this.inputEvents();
    return !!input && input.size() > 0;
  });

  // Computed: use input if provided, otherwise use fetched
  events = computed(() => {
    const sourceList = this.hasInputEvents()
      ? this.inputEvents()!
      : this.fetchedEvents();

    return new List<EventSummary>([...sourceList.toArray()]);
  });

  ngOnInit(): void {
    // Setup search debounce
    this.searchSubject.pipe(
      debounceTime(environment.defaultSearchDebounceTime),
    ).subscribe((query) => {
      this.searchQuery.set(query);
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
    // Build filters with query
    const filters = { ...this.eventFilters() };

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
      },
      error: (err) => console.error('Error fetching events:', err)
    });
  }

  onSort(field: SortField): void {
    if (field === this.sortField()) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

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

  protected readonly routes = routes;
}
