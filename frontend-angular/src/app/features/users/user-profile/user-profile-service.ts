import { Injectable, inject, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import { AuthService } from '../../auth/services/auth-service';
import { UrlUtilService } from '../../../shared/utils/url/url-util-service';
import { environment } from '../../../../environments/environment';
import { UserProfile } from './models/user-profile-model';
import { User } from '../../auth/models/user';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  private apiUrl = `${environment.usersEndpoint}`;

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
    return this.http.get<User>(`${this.apiUrl}/${username}`)
      .pipe(tap(user => this.viewedUserSignal.set(user)));
  }

  setViewedUser(user: User): void {
    this.viewedUserSignal.set(user);
  }

  updateField(fieldName: string, value: any): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/profile`, { [fieldName]: value })
      .pipe(
        tap(updatedUser => {
          // Update localStorage to keep AuthService in sync
          localStorage.setItem('current_user', JSON.stringify(updatedUser));

          const loggedInUser = this.authService.currentUser();
          if (loggedInUser?.username === updatedUser.username) {
            this.viewedUserSignal.set(updatedUser);
          }
        })
      );
  }

  updatePassword(password: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/password`, { password });
  }

  updateProfilePicture(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/profile-picture`, formData)
      .pipe(
        tap(updatedUser => {
          localStorage.setItem('current_user', JSON.stringify(updatedUser));

          const loggedInUser = this.authService.currentUser();
          if (loggedInUser?.username === updatedUser.username) {
            this.viewedUserSignal.set(updatedUser);
          }
        })
      );
  }
}
