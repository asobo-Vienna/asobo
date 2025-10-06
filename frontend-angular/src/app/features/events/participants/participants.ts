import {Component, Input} from '@angular/core';
import {Participant} from '../models/participant';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-participants',
  imports: [],
  templateUrl: './participants.html',
  styleUrl: './participants.scss'
})
export class Participants {
  @Input() participants!: List<Participant>;
  protected readonly UrlUtilService = UrlUtilService;
}
