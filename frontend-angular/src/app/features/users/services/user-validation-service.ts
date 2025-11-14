// services/user-validation.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserValidationService {
  private http = inject(HttpClient);

  checkUsernameAvailability(username: string): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiBaseUrl}/auth/check-username/${username}`);
  }

  checkEmailAvailability(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiBaseUrl}/auth/check-email/${email}`);
  }
}
