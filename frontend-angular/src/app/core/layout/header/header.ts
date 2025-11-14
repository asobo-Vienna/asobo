import { Component, inject } from '@angular/core';
import {RouterLink, Router} from '@angular/router';
import {AuthService} from '../../../features/auth/services/auth-service';
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {UserProfileService} from '../../../features/users/services/user-profile-service';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  private router = inject(Router);
  authService = inject(AuthService);

  goHome() {
    console.log('Logo clicked');
    this.router.navigate(['/']);
  }

  get loggedInUserProfile() {
    return this.authService.loggedInUserFormatted();
  }

  protected readonly environment = environment;
}
