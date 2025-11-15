import { Component } from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {CreateEventForm} from '../create-event-form/create-event-form';

@Component({
  selector: 'app-create-event-page',
  imports: [
    ReactiveFormsModule,
    CreateEventForm,
  ],
  templateUrl: './create-event-page.html',
  styleUrl: './create-event-page.scss'
})
export class CreateEventPage {
}
