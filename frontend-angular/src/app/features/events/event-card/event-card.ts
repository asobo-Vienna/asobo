import {Component, inject, input, output} from '@angular/core';
import {AsyncPipe, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {Tag} from 'primeng/tag';
import {AuthService} from '../../auth/services/auth-service';
import {EventSummary} from '../models/event-summary';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {environment} from '../../../../environments/environment';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {List} from '../../../core/data-structures/lists/list';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.html',
  imports: [
    DatePipe,
    RouterLink,
    Tag,
    AsyncPipe,
    SecureImagePipe
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
  protected readonly environment = environment;
}


