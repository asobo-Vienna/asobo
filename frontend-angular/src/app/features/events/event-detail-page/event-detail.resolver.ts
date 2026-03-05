import {inject} from '@angular/core';
import {ResolveFn, Router} from '@angular/router';
import {catchError, EMPTY} from 'rxjs';
import {EventService} from '../services/event-service';
import {Event} from '../../../shared/entities/events/event';

export const eventDetailResolver: ResolveFn<Event> = (route) => {
  const router = inject(Router);
  return inject(EventService).getEventById(route.paramMap.get('id')!).pipe(
    catchError(() => {
      router.navigate(['/events']);
      return EMPTY;
    })
  );
};
