import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.html',
  styleUrl: './event-card.scss'
})
export class EventCard {
  @Input() title!: string;
  @Input() pictureURI!: string;
  @Input() date!: string;
  @Input() time!: string;
  @Input() location!: string;
  @Input() link?: string;
}


