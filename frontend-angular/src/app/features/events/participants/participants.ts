import {Component, Input} from '@angular/core';
import {Participant} from '../models/participant';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from "../../../../environments/environment";
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-participants',
  templateUrl: './participants.html',
  imports: [
    RouterLink
  ],
  styleUrl: './participants.scss'
})
export class Participants {
  @Input() participants!: List<Participant>;
  protected readonly UrlUtilService = UrlUtilService;
    protected readonly environment = environment;
}
