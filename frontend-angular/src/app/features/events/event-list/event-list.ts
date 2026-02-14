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

@Component({
  selector: 'app-event-list',
  imports: [
    EventCard,
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

  // default sort order: descending by date
  sortField = signal<SortField>('date');
  sortDirection = signal<'asc' | 'desc'>('desc');

  hasInputEvents = computed(() => {
    const input = this.inputEvents();
    return !!input && input.size() > 0;
  });

  // Computed: use input if provided, otherwise use fetched
  events = computed(() => {
    const sourceList = this.hasInputEvents()
      ? this.inputEvents()!
      : this.fetchedEvents();

    const sorted = [...sourceList.toArray()].sort((a, b) => {
      const field = this.sortField();
      const dir = this.sortDirection();

      let result = 0;

      switch (field) {
        case 'date':
          result = a.date.localeCompare(b.date);
          break;

        case 'title':
          result = a.title.localeCompare(b.title);
          break;

        case 'location':
          result = a.location.localeCompare(b.location);
          break;

        case 'isPrivateEvent':
          // primary visibility sort order
          result = dir === 'asc'
            ? Number(a.isPrivate) - Number(b.isPrivate) // public first
            : Number(b.isPrivate) - Number(a.isPrivate); // private first

          // secondary: date within visibility group always descending
          if (result === 0) {
            result = b.date.localeCompare(a.date);
          }

          return result;
      }

      // Use sortDirection for all other fields
      return dir === 'asc' ? result : -result;
    });

    return new List<EventSummary>(sorted);
  });

  ngOnInit(): void {
    // Only fetch if no input was provided
    if (!this.hasInputEvents()) {
      this.fetchEvents();
    }
  }

  private fetchEvents(): void {
    //let params = this.
    const params = {
      page: 0,
      size: 100,
      sort: `${this.sortField()},${this.sortDirection()}`
    };

    if (this.authService.isLoggedIn()) {
      this.eventService.getAllEventsPaginated(params, this.eventFilters()).subscribe({
        next: (events) => this.fetchedEvents.set(new List<EventSummary>(events.content)),
        error: (err) => console.error('Error fetching events:', err)
      });
    } else {
      this.eventFilters().isPrivateEvent = false;
      this.eventService.getAllEventsPaginated(params, this.eventFilters()).subscribe({
        next: (events) => this.fetchedEvents.set(new List<EventSummary>(events.content)),
        error: (err) => console.error('Error fetching public events:', err)
      });
    }
  }

  onSort(field: SortField): void {
    if (field === this.sortField()) {
      // Toggle direction
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

    // reload only if events come from backend
    if (!this.hasInputEvents()) {
      this.fetchEvents();
    }
  }

  getSortIcon(field: SortField): string {
    if (field !== this.sortField()) return '';
    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }

  protected readonly routes = routes;
}
