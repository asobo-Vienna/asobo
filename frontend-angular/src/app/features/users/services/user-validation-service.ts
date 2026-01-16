// services/user-validation.service.ts
import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, take} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {AvailabilityResponse} from '../../../shared/entities/availability-response';

@Injectable({providedIn: 'root'})
export class UserValidationService {
  private http = inject(HttpClient);

  checkUsernameAvailability(username: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${environment.apiBaseUrl}/auth/check-username`, { params: { username } })
      .pipe(take(1));
  }

  checkEmailAvailability(email: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${environment.apiBaseUrl}/auth/check-email`, { params: { email } })
      .pipe(take(1));
  }
}
