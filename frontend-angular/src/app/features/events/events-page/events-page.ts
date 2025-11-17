import {Component, inject, OnInit} from '@angular/core';
import {EventCard} from '../event-card/event-card';
import {EventService} from '../services/event-service';
import {Event} from '../models/event'
import {AuthService} from '../../auth/services/auth-service';

@Component({
  selector: 'app-events-page',
  imports: [
    EventCard,
  ],
  templateUrl: './events-page.html',
  styleUrl: './events-page.scss'
})
export class EventsPage implements OnInit {
  private eventService = inject(EventService);
  authService = inject(AuthService);
  events: Event[] = [];

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.eventService.getAllEvents().subscribe({
        next: (events) => this.events = events,
        error: (err) => console.error('Error fetching events:', err)
      });
      return;
    }

    this.eventService.getAllPublicEvents().subscribe({
      next: (events) => this.events = events,
      error: (err) => console.error('Error fetching public events:', err)
    });
  }
}
