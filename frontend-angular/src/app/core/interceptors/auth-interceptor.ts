import {inject, Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private router = inject(Router);

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem(environment.JWT_TOKEN_STORAGE_KEY);

    const publicEndpoints = ['/api/auth/login', '/api/auth/register'];

    const urlPath = this.getPath(req.url);

    // 1. Skip auth for public endpoints
    if (publicEndpoints.includes(urlPath)) {
      return next.handle(req);
    }

    // 2. Clone request safely
    let modifiedReq = req;

    const isFormData = req.body instanceof FormData;

    if (token) {
      modifiedReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // IMPORTANT: NEVER touch Content-Type for FormData
    // Angular/browser must set boundary automatically

    return next.handle(modifiedReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          localStorage.removeItem(environment.JWT_TOKEN_STORAGE_KEY);

          this.router.navigate(['/login'], {
            queryParams: {
              returnUrl: this.router.url,
              expired: true
            }
          });
        }

        if (error.status === 403) {
          console.warn('Access denied:', req.url);
        }

        return throwError(() => error);
      })
    );
  }

  private getPath(url: string): string {
    try {
      // absolute URL
      if (url.startsWith('http')) {
        return new URL(url).pathname;
      }

      // relative URL
      return url;
    } catch {
      return url;
    }
  }
}
