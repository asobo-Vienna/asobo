import { Component } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {EventCard} from '../event-card/event-card';
import moment from 'moment';

interface Event {
  id: string;
  title: string;
  pictureURI: string;
  date: string;
  time: string;
  location: string;
}


@Component({
  selector: 'app-events-page',
  imports: [
    EventCard,
  ],
  templateUrl: './events-page.html',
  styleUrl: './events-page.scss'
})
export class EventsPage {
  events: Event[] = [];
  private EVENTSADDRESS = 'http://127.0.0.1:8080/api/events';

  constructor(private http: HttpClient) {
    console.log('EventsPage component constructed');
  }

  ngOnInit(): void {
    this.getAllEvents();
  }

  async getAllEvents(): Promise<void> {
    this.http.get<Event[]>(this.EVENTSADDRESS).subscribe({
      next: (events) => {
        this.events = events.map(event => {
          return {
            ...event,
            date: moment(event.date).format('ddd, MMMM D, YYYY'),
            time: moment(event.date).format('h:mm a'),
          };
        });
        },
      error: (err) => console.error('Error:', err)
    });
  }
}
