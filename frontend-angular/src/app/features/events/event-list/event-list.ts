import {Component, computed, inject, input, OnInit, signal} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {Event} from '../models/event'
import {AuthService} from '../../auth/services/auth-service';
import {List} from '../../../core/data_structures/lists/list';
import {EventSummary} from '../models/event-summary';

type SortField = 'date' | 'title' | 'location' | 'isPrivateEvent';

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

  inputEvents = input<List<EventSummary>>();
  private fetchedEvents = signal<List<EventSummary>>(new List<EventSummary>());

  // default sort order: descending by date
  sortField = signal<SortField>('date');
  sortDirection = signal<'asc' | 'desc'>('desc');

  // Computed: use input if provided, otherwise use fetched
  events = computed(() => {
    const input = this.inputEvents();
    return input && input.size() > 0 ? input : this.fetchedEvents();
  });

  ngOnInit(): void {
    // Only fetch if no input was provided
    if (!this.inputEvents()) {
      this.fetchEvents();
    }
  }

  private fetchEvents(): void {
    const params = {
      page: 0,
      size: 100,
      sort: `${this.sortField()},${this.sortDirection()}`
    };

    if (this.authService.isLoggedIn()) {
      /*this.eventService.getAllEvents().subscribe({
        next: (events) => this.fetchedEvents.set(new List<EventSummary>(events)),
        error: (err) => console.error('Error fetching events:', err)
      });*/
      this.eventService.getAllEventsPaginated(params).subscribe({
        next: (events) => this.fetchedEvents.set(new List<EventSummary>(events.content)),
        error: (err) => console.error('Error fetching events:', err)
      });
    } else {
      /*this.eventService.getAllPublicEvents().subscribe({
        next: (events) => this.fetchedEvents.set(new List<EventSummary>(events)),
        error: (err) => console.error('Error fetching public events:', err)
      });*/
      this.eventService.getAllPublicEventsPaginated(params).subscribe({
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
    if (!this.inputEvents()) {
      this.fetchEvents();
    }
  }

  getSortIcon(field: SortField): string {
    if (field !== this.sortField()) return '';
    return this.sortDirection() === 'asc' ? '↑' : '↓';
  }
}
