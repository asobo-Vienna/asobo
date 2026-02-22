import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../features/auth/services/auth-service';
import {environment} from '../../../../environments/environment';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {GlobalSearch} from '../../../features/search/global-search/global-search';
import {AsyncPipe} from '@angular/common';
import {SecureImagePipe} from '../../pipes/secure-image-pipe';


@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    GlobalSearch,
    AsyncPipe,
    SecureImagePipe,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  private router = inject(Router);
  authService = inject(AuthService);
  accessControlService = inject(AccessControlService);

  goHome() {
    console.log('Logo clicked');
    this.router.navigate(['/']);
  }

  get loggedInUserProfile() {
    return this.authService.loggedInUserFormatted();
  }

  protected readonly environment = environment;
}
