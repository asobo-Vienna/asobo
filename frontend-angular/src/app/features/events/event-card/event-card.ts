import {Component, inject, input, Input, output} from '@angular/core';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {List} from '../../../core/data-structures/lists/list';
import {Participant} from '../models/participant';
import {Comment} from '../models/comment';
import {Event} from '../models/event';
import { Tag } from 'primeng/tag';
import {AuthService} from '../../auth/services/auth-service';
import {EventSummary} from '../models/event-summary';
import {AccessControlService} from '../../../shared/services/access-control-service';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.html',
  imports: [
    DatePipe,
    RouterLink,
    Tag
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
}


