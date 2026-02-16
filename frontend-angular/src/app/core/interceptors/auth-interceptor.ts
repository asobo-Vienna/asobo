import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private router = inject(Router);

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem(environment.JWT_TOKEN_STORAGE_KEY);
    
    const publicEndpoints = ['/api/auth/login', '/api/auth/register'];

    // Extract pathname to ignore host/port/query
    const urlPath = new URL(req.url, window.location.origin).pathname;

    // Skip JWT for public endpoints
    if (publicEndpoints.includes(urlPath)) {
      return next.handle(req);
    }

    // If token exists, add it (even for /api/search)
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });

      return next.handle(cloned).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            localStorage.removeItem(environment.JWT_TOKEN_STORAGE_KEY);
            console.log('Session expired. Please login again.');
            this.router.navigate(['/login'], {
              queryParams: { returnUrl: this.router.url, expired: true }
            });
          } else if (error.status === 403) {
            console.warn('Access denied to', req.url);
          }
          return throwError(() => error);
        })
      );
    }

    // No token: send request without Authorization header
    // (Backend will treat as unauthenticated → no private events)
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Don't redirect to login for 401 on search
        if (error.status === 401 && !urlPath.startsWith('/api/search')) {
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
