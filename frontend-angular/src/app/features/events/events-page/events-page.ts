import {Component} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {Event} from '../models/event'

@Component({
  selector: 'app-events-page',
  imports: [
    EventCard,
  ],
  templateUrl: './events-page.html',
  styleUrl: './events-page.scss'
})
export class EventsPage {
  constructor(private eventService: EventService) {
  }

  events: Event[] = [];

  ngOnInit(): void {
    this.eventService.getAllEvents().subscribe({
      next: (events) => this.events = events,
      error: (err) => console.error('Error fetching events:', err)
    });
  }
}
