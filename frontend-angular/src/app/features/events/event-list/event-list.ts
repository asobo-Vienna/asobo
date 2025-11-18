import {Component, computed, inject, input, OnInit, signal} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {Event} from '../models/event'
import {AuthService} from '../../auth/services/auth-service';
import {List} from '../../../core/data_structures/lists/list';

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

  inputEvents = input<List<Event>>();
  private fetchedEvents = signal<List<Event>>(new List<Event>());

  // Computed: use input if provided, otherwise use fetched
  events = computed(() => {
    const input = this.inputEvents();
    return input && input.size() > 0 ? input : this.fetchedEvents();
  });

  ngOnInit(): void {
    // Only fetch if no input was provided
    if (!this.inputEvents() || this.inputEvents()!.size() === 0) {
      if (this.authService.isLoggedIn()) {
        this.eventService.getAllEvents().subscribe({
          next: (events) => this.fetchedEvents.set(new List<Event>(events)),
          error: (err) => console.error('Error fetching events:', err)
        });
      } else {
        this.eventService.getAllPublicEvents().subscribe({
          next: (events) => this.fetchedEvents.set(new List<Event>(events)),
          error: (err) => console.error('Error fetching public events:', err)
        });
      }
    }
  }
}
