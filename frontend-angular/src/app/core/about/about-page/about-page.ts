import {Component} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {SecureImagePipe} from '../../pipes/secure-image-pipe';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-about-page',
  imports: [
    SecureImagePipe,
    AsyncPipe
  ],
  templateUrl: './about-page.html',
  styleUrl: './about-page.scss'
})
export class AboutPage {

  protected readonly environment = environment;
  protected readonly UrlUtilService = UrlUtilService;
}
