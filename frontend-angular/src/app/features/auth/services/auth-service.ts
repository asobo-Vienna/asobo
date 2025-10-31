import {computed, Injectable, signal, inject} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from '@angular/router';
import {User} from './models/user';
import {LoginResponse} from './models/login-response';
import {environment} from '../../../environments/environment';
import {jwtDecode} from 'jwt-decode';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_KEY = 'current_user';

  private tokenCheckInterval: any;
  // Signal to track current user state
  private currentUserSignal = signal<User | null>(this.getUserFromStorage());

  // Public readonly signals
  public currentUser = this.currentUserSignal.asReadonly();
  public isLoggedIn = computed(() => !!this.currentUser() && !!this.getToken() && !this.isTokenExpired());

  private http = inject(HttpClient);
  private router = inject(Router);

  constructor() {
    // Check token validity on app load
    this.checkTokenValidity();

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
    // Store JWT token
    localStorage.setItem(this.TOKEN_KEY, authResult.token);

    // Store user object
    localStorage.setItem(this.USER_KEY, JSON.stringify(authResult.user));

    // Update signal
    this.currentUserSignal.set(authResult.user);
  }

  logout(): void {
    // Clear storage
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    // Update signal
    this.currentUserSignal.set(null);

    // Stop token check
    this.stopTokenValidityCheck();

    // Navigate to login
    this.router.navigate(['/login']);
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
    // Clear any existing interval
    this.stopTokenValidityCheck();

    // Check more frequently - every 10 seconds
    this.tokenCheckInterval = setInterval(() => {
      const token = this.getToken();

      if (!token) {
        this.logout();
        return;
      }

      try {
        const decoded: any = jwtDecode(token);
        const currentTime = Date.now() / 1000;
        const timeUntilExpiry = decoded.exp - currentTime;

        // Warning 1 minute (60 seconds) before expiration
        if (timeUntilExpiry <= 60 && timeUntilExpiry > 0) {
          console.warn(`Token expiring in ${Math.floor(timeUntilExpiry)} seconds!`);
          // You can show a toast/notification here
        }

        // Logout if expired
        if (timeUntilExpiry <= 0) {
          console.log('Token expired, logging out...');
          this.logout();
        }
      } catch (error) {
        console.error('Error checking token validity:', error);
        this.logout();
      }
    }, 10000); // Check every 10 seconds instead of 60
  }

  private stopTokenValidityCheck(): void {
    if (this.tokenCheckInterval) {
      clearInterval(this.tokenCheckInterval);
      this.tokenCheckInterval = null;
    }
  }

  checkUsernameAvailability(username: string): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiBaseUrl}/auth/check-username/${username}`);
  }

  checkEmailAvailability(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiBaseUrl}/auth/check-email/${email}`);
  }

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
