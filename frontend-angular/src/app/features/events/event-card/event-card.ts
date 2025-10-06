import {Component, Input} from '@angular/core';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

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
  @Input() id!: string;
  @Input() title!: string;
  @Input() pictureURI!: string;
  @Input() date!: string;
  @Input() time!: string;
  @Input() location!: string;
  @Input() link?: string;
  protected readonly UrlUtilService = UrlUtilService;
}


