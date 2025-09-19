import { Component } from '@angular/core';
import {Header} from './core/layout/header/header';
import {Footer} from './core/layout/footer/footer';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [
    Header,
    Footer,
    RouterOutlet
  ],
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

}
