import { Injectable, inject, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import { AuthService } from '../../auth/services/auth-service';
import { UrlUtilService } from '../../../shared/utils/url/url-util-service';
import { environment } from '../../../../environments/environment';
import { UserProfile } from '../user-profile/models/user-profile-model';
import { User } from '../../auth/models/user';
import {LoginResponse} from '../../auth/models/login-response';
import {UserService} from './user-service';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private http = inject(HttpClient);
  private usersEndpointBase = `${environment.usersEndpoint}`;

  private viewedUserSignal = signal<User | null>(null);

  userProfile = computed<UserProfile>(() => {
    const user = this.viewedUserSignal();
    return {
      userProfileUrl: user?.username
        ? `${environment.userProfileBaseUrl}${user.username}`
        : '/login',
      pictureUrl: user?.pictureURI
        ? UrlUtilService.getMediaUrl(user.pictureURI)
        : UrlUtilService.getMediaUrl(environment.userDummyProfilePicRelativeUrl),
      pictureAlt: user?.username
        ? `${user.username}'s profile picture`
        : 'User profile picture',
      username: user?.username || 'Guest'
    };
  });

  getUserByUsername(username: string): Observable<User> {
    const loggedInUser = this.authService.currentUser();

    if (loggedInUser?.username === username) {
      // Viewing own profile - use AuthService data
      this.viewedUserSignal.set(loggedInUser);
      return of(loggedInUser);
    }

    // Viewing someone else's profile - fetch from backend
    return this.http.get<User>(`${this.usersEndpointBase}/${username}`)
      .pipe(tap(user => this.viewedUserSignal.set(user)));
  }

  setViewedUser(user: User): void {
    this.viewedUserSignal.set(user);
  }

  // TODO: still needs to be implemented correctly
  updateField(fieldName: string, value: any): Observable<LoginResponse> {
    const loggedInUser = this.authService.currentUser();
    console.log("username: ", loggedInUser?.username)
    return this.http.patch<LoginResponse>(`${environment.apiBaseUrl}/users/${loggedInUser?.id}`, { [fieldName]: value })
      .pipe(
        tap(response => {
          // Update localStorage to keep AuthService in sync
          localStorage.setItem('current_user', JSON.stringify(response.user));
          if (response.token) {
            localStorage.setItem(environment.JWT_TOKEN_STORAGE_KEY, response.token);
          }

          if (loggedInUser?.username === response.user.username) {
            this.viewedUserSignal.set(response.user);
          }
        })
      );
  }

  updatePassword(password: string): Observable<LoginResponse> {
    return this.userService.updatePassword(password);
  }

  updateProfilePicture(formData: FormData): Observable<LoginResponse> {
    return this.userService.updateProfilePicture(formData)
      .pipe(
        tap(response => {
          this.authService.updateUserInStorage(response.user);

          const loggedInUser = this.authService.currentUser();
          if (loggedInUser?.username === response.user.username) {
            this.viewedUserSignal.set(response.user);
          }
        })
      );
  }
}
