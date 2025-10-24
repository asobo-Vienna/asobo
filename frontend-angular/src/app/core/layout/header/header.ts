import { Component, inject } from '@angular/core';
import {RouterLink, Router} from '@angular/router';
import {AuthService} from '../../../features/auth/auth-service';
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

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
    console.log('Logo clicked');  // 🔹 add this to test
    this.router.navigate(['/']);
  }

  get userProfile() {
    const user = this.authService.currentUser();
    return {
      userProfileUrl: user?.username
        ? `${environment.userProfileBaseUrl}${user?.username}`
        : '/login',
      pictureUrl: user?.pictureURI
        ? UrlUtilService.getMediaUrl(user.pictureURI)
        : UrlUtilService.getMediaUrl(environment.userDummyProfilePicRelativeUrl),
      pictureAlt: user?.username
        ? `${user.username}'s profile picture`
        : 'User profile picture',
      username: user?.username || 'Guest'
    };
  }
}
