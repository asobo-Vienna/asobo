import {Component, input, Input} from '@angular/core';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {List} from '../../../core/data_structures/lists/list';
import {Participant} from '../models/participant';
import {Comment} from '../models/comment';
import {Event} from '../models/event';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.html',
  imports: [
    DatePipe,
    RouterLink
  ],
  styleUrl: './event-card.scss'
})
export class EventCard {
  event = input<Event>({
    id: '',
    title: '',
    pictureURI: '',
    date: '',
    time: '',
    location: '',
    description: '',
    isPrivate: false,
    participants: new List<Participant>(),
    comments: new List<Comment>()
  });
  protected readonly UrlUtilService = UrlUtilService;
}


