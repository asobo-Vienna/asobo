import {Component} from '@angular/core';
import {Header} from './core/layout/header/header';
import {Footer} from './core/layout/footer/footer';
import {RouterOutlet} from '@angular/router';
import {Toast} from 'primeng/toast';
import {ConfirmDialogModule} from 'primeng/confirmdialog';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    Header,
    Footer,
    RouterOutlet,
    Toast,
    ConfirmDialogModule,
  ],
  styleUrls: ['./app.scss']
})
export class App {

}
