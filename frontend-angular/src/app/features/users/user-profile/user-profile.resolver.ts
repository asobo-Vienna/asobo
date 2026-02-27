import {inject} from '@angular/core';
import {ResolveFn, Router} from '@angular/router';
import {EMPTY, catchError} from 'rxjs';
import {UserProfileService} from '../services/user-profile-service';
import {User} from '../../auth/models/user';

export const userProfileResolver: ResolveFn<User> = (route) => {
  const router = inject(Router);
  return inject(UserProfileService).getUserByUsername(route.paramMap.get('username')!).pipe(
    catchError(() => {
      router.navigate(['/events']);
      return EMPTY;
    })
  );
};
