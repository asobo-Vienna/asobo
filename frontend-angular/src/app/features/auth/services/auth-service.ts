import {computed, Injectable, signal, inject} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from '@angular/router';
import {User} from '../models/user';
import {LoginResponse} from '../models/login-response';
import {environment} from '../../../../environments/environment';
import {jwtDecode} from 'jwt-decode';
import {UserProfile} from '../../users/user-profile/models/user-profile-model';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = environment.JWT_TOKEN_STORAGE_KEY;
  private readonly USER_KEY = environment.USER_STORAGE_KEY;

  private tokenCheckInterval: any;
  // Signal to track current user state
  private currentUserSignal = signal<User | null>(this.getUserFromStorage());

  // Public readonly signals
  public currentUser = this.currentUserSignal.asReadonly();
  public isLoggedIn = computed(() => !!this.currentUser() && !!this.getToken() && !this.isTokenExpired());

  private http = inject(HttpClient);
  private router = inject(Router);

  constructor() {
    // Start periodic token check
    this.startTokenValidityCheck();
  }

  login(credentials: { identifier: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.loginEndpoint, credentials)
      .pipe(
        tap(response => {
          this.setSession(response);
          this.startTokenValidityCheck();
        })
      );
  }

  register(formData: FormData): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.registerEndpoint, formData)
      .pipe(
        tap(response => {
          this.setSession(response);
          this.startTokenValidityCheck();
        })
      );
  }

  private setSession(authResult: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, authResult.token);

    // Store user object
    localStorage.setItem(this.USER_KEY, JSON.stringify(authResult.user));

    this.currentUserSignal.set(authResult.user);
  }

  logout(navigate: boolean = true): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    this.currentUserSignal.set(null);

    this.stopTokenValidityCheck();

    if (navigate) {
      this.router.navigate(['/login']);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch (e) {
        console.error('Error parsing user from storage', e);
        return null;
      }
    }
    return null;
  }

  updateUserInStorage(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUserSignal.set(user);
  }

  private checkTokenValidity(): void {
    const token = this.getToken();

    if (!token) {
      this.currentUserSignal.set(null);
      return;
    }

    if (this.isTokenExpired(token)) {
      console.log('Token expired, logging out...');
      this.logout();
    }
  }

  private isTokenExpired(token?: string): boolean {
    const tokenToCheck = token || this.getToken();

    if (!tokenToCheck) return true;

    try {
      const decoded: any = jwtDecode(tokenToCheck);
      const currentTime = Date.now() / 1000;
      return decoded.exp < currentTime;
    } catch {
      return true;
    }
  }

  private startTokenValidityCheck(): void {
    this.stopTokenValidityCheck();

    if (this.isTokenExpired()) {
      this.logout(false); // Add false here to prevent navigation on app init
      return;
    }

    this.tokenCheckInterval = setInterval(() => {
      if (!this.getToken()) {
        this.logout(); // Keep default (true) here for active expiration
        return;
      }

      // Get time until expiry for warning
      const token = this.getToken()!;
      try {
        const decoded: any = jwtDecode(token);
        const timeUntilExpiry = decoded.exp - (Date.now() / 1000);

        if (0 < timeUntilExpiry && timeUntilExpiry <= 60) {
          console.warn(`Token expiring in ${Math.floor(timeUntilExpiry)} seconds!`);
        }
      } catch (error) {
        console.error('Error checking token validity:', error);
      }

      if (this.isTokenExpired()) {
        this.logout(); // Keep default (true) here for active expiration
      }
    }, 10000);
  }

  private stopTokenValidityCheck(): void {
    if (this.tokenCheckInterval) {
      clearInterval(this.tokenCheckInterval);
      this.tokenCheckInterval = null;
    }
  }

  loggedInUserFormatted = computed<UserProfile>(() => {
    const user = this.currentUser();
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

  // Refresh user data from backend
  /*refreshUser(): Observable<User> {
    return this.http.get<User>('/api/auth/me')
      .pipe(
        tap(user => {
          localStorage.setItem(this.USER_KEY, JSON.stringify(user));
          this.currentUserSignal.set(user);
        })
      );
  }*/
}
