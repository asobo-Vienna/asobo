import {inject} from '@angular/core';
import {ResolveFn, Router} from '@angular/router';
import {catchError, EMPTY} from 'rxjs';
import {UserProfileService} from '../services/user-profile-service';
import {User} from '../../../shared/entities/users/user';

export const userProfileResolver: ResolveFn<User> = (route) => {
  const router = inject(Router);
  return inject(UserProfileService).getUserByUsername(route.paramMap.get('username')!).pipe(
    catchError(() => {
      router.navigate(['/events']);
      return EMPTY;
    })
  );
};
