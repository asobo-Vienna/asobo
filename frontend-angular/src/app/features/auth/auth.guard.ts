import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import {AuthService} from './auth-service';


export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect to login with return URL
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};

// Optional: Guard to prevent logged-in users from accessing login page
export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true;
  }

  // Already logged in, redirect to dashboard
  return router.createUrlTree(['/dashboard']);
};
