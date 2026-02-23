import {Component, Input} from '@angular/core';
import {Participant} from '../models/participant';
import {List} from '../../../core/data-structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {environment} from "../../../../environments/environment";
import {RouterLink} from '@angular/router';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-participants',
  templateUrl: './participants.html',
  imports: [
    RouterLink,
    SecureImagePipe,
    AsyncPipe
  ],
  styleUrl: './participants.scss'
})
export class Participants {
  @Input() participants!: List<Participant>;
  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}
