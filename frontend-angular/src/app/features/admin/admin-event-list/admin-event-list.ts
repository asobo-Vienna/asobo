import {Component, inject, signal} from '@angular/core';
import {AdminService} from '../services/admin-service';
import {Event} from '../../events/models/event';
import {environment} from '../../../../environments/environment';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {EventService} from '../../events/services/event-service';
import {TableModule} from 'primeng/table';
import {DatePipe} from '@angular/common';
import {Tag} from 'primeng/tag';

@Component({
  selector: 'app-admin-event-list',
  imports: [
    RouterLink,
    TableModule,
    DatePipe,
    Tag
  ],
  templateUrl: './admin-event-list.html',
  styleUrl: './admin-event-list.scss',
})
export class AdminEventList {
  private eventService = inject(EventService);
  events = signal<Event[]>([]);

  ngOnInit(): void {
    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        console.log(this.events());
      },
      error: (err) => console.error('Error fetching events:', err)
    });
    return;
  }

  onEdit(event: any) {
    console.log('Editing event:', event);
  }

  onDelete(event: any) {
    console.log('Deleting event:', event);
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}
