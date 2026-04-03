import {Component, inject, input, output} from '@angular/core';
import {AsyncPipe, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {DateUtils} from '../../../shared/utils/date/date-utils';
import {Tag} from 'primeng/tag';
import {AuthService} from '../../auth/services/auth-service';
import {EventSummary} from '../../../shared/entities/events/event-summary';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {environment} from '../../../../environments/environment';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {List} from '../../../core/data-structures/lists/list';
import {Badge} from 'primeng/badge';
import {EventCategory} from '../../../shared/entities/events/event-category';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.html',
  imports: [
    DatePipe,
    RouterLink,
    Tag,
    AsyncPipe,
    SecureImagePipe,
    Badge
  ],
  styleUrl: './event-card.scss'
})
export class EventCard {
  event = input<EventSummary>({
    id: '',
    title: '',
    pictureURI: '',
    date: '',
    time: '',
    location: '',
    categories: new List<EventCategory>,
    description: '',
    isPrivateEvent: false,
    participantCount: 0,
    commentCount: 0,
    mediaCount: 0,
    eventAdminIds: new List<string>(),
  });
  protected readonly UrlUtilService = UrlUtilService;
  authService = inject(AuthService);
  accessControlService = inject(AccessControlService);

  eventDeleted = output<EventSummary>();

  isEventInThePast(): boolean {
    const date = this.event().date;
    return date ? DateUtils.isDateInThePast(new Date(date)) : false;
  }

  showDeleteButton(): boolean {
    if (!this.authService.isLoggedIn()) return false;
    if (this.isEventInThePast()) return false;
    const currentUserId = this.accessControlService.getCurrentUser()?.id;
    const event = this.event();
    return this.accessControlService.hasAdminAccess()
      || currentUserId === event.creator?.id
      || currentUserId === event.creatorId
      || this.accessControlService.isCurrentUserEventAdmin(event);
  }

  protected readonly environment = environment;
}


