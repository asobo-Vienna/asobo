import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {AuthService} from '../../../features/auth/auth-service';
import {environment} from '../../../../environments/environment';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    NgOptimizedImage
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  constructor(public authService: AuthService,) {
  }

  get userProfile() {
    const user = this.authService.currentUser();
    return {
      userProfileUrl: user?.username
        ? `${environment.userProfileBaseUrl}${user?.username}`
        : '/login',
      pictureUrl: user?.pictureURI
        ? `${environment.backendUrl}${user.pictureURI}`
        : '/images/default-avatar.png',
      pictureAlt: user?.username
        ? `${user.username}'s profile picture`
        : 'User profile picture',
      username: user?.username || 'Guest'
    };
  }

  protected readonly environment = environment;
}
