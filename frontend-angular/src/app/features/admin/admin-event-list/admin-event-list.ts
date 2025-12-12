import {Component, inject, OnInit, signal} from '@angular/core';
import {Event} from '../../events/models/event';
import {environment} from '../../../../environments/environment';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {EventService} from '../../events/services/event-service';
import {TableModule} from 'primeng/table';
import {DatePipe} from '@angular/common';
import {Tag} from 'primeng/tag';
import {Dialog} from 'primeng/dialog';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-admin-event-list',
  imports: [
    RouterLink,
    TableModule,
    DatePipe,
    Tag,
    Dialog,
    Button
  ],
  templateUrl: './admin-event-list.html',
  styleUrl: './admin-event-list.scss',
})
export class AdminEventList implements OnInit {
  private eventService = inject(EventService);
  events = signal<Event[]>([]);
  loading = true;

  showDescriptionDialog = false;
  selectedEvent: Event | null = null;

  ngOnInit(): void {
    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching events:', err);
        this.loading = false;
      }
    });
  }

  onEdit(event: Event) {
    console.log('Editing event:', event);
  }

  onDelete(event: Event) {
    console.log('Deleting event:', event);
  }

  viewFullDescription(event: Event): void {
    this.selectedEvent = event;
    this.showDescriptionDialog = true;
  }

  getEventRouterLink(eventId: string): string {
    return `${environment.eventsSectionBaseUrl}/${eventId}`;
  }

  getEventDescriptionPreview(description: string): string {
    if (description.length > environment.eventDescriptionPreviewLength) {
      return `${description.substring(0, environment.eventDescriptionPreviewLength - 1)}...`;
    }
    return description;
  }

  shouldShowViewMore(description: string): boolean {
    return description.length > environment.eventDescriptionPreviewLength;
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}
