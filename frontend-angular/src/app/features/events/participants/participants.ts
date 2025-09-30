import {Component, Input} from '@angular/core';
import {Participant} from '../models/participant';

@Component({
  selector: 'app-participants',
  imports: [],
  templateUrl: './participants.html',
  styleUrl: './participants.scss'
})
export class Participants {
  @Input() participants!: Participant[];
}
