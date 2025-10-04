import { Component } from '@angular/core';
import {AuthService} from '../../auth/auth-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-profile',
  imports: [],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss'
})
export class UserProfile {
  constructor(public authService: AuthService, private router: Router) {}
}
