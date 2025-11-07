import { Component, inject, OnInit } from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {RegistrationForm} from '../registration-form/registration-form';
import {RouterLink, Router} from "@angular/router";
import {AuthService} from '../../services/auth-service';

@Component({
  selector: 'app-registration-page',
  imports: [
    ReactiveFormsModule,
    RegistrationForm,
    RouterLink,
  ],
  templateUrl: './registration-page.html',
  styleUrl: './registration-page.scss'
})
export class RegistrationPage implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);

  ngOnInit(): void {
      if(this.authService.isLoggedIn()){
        this.router.navigate(['/events']);
      }
  }


}
