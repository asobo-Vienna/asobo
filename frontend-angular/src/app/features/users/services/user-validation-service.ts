// services/user-validation.service.ts
import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {AvailabilityResponse} from '../../../shared/entities/availability-response';

@Injectable({providedIn: 'root'})
export class UserValidationService {
  private http = inject(HttpClient);

  checkUsernameAvailability(username: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${environment.apiBaseUrl}/auth/check-username`, { params: { username } });
  }

  checkEmailAvailability(email: string): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(`${environment.apiBaseUrl}/auth/check-email`, { params: { email } });
  }
}
