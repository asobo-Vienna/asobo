import { Component, inject, OnInit } from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {AuthService} from '../../auth/services/auth-service';
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
export class CreateEventPage implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);

  ngOnInit(): void {
  }
}
