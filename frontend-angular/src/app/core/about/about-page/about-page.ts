import { Component } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-about-page',
  imports: [],
  templateUrl: './about-page.html',
  styleUrl: './about-page.scss'
})
export class AboutPage {

  protected readonly environment = environment;
  protected readonly UrlUtilService = UrlUtilService;
}
