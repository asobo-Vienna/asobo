import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {map, Observable, take, tap, throwError} from 'rxjs';
import {LoginResponse} from '../../auth/models/login-response';
import {AuthService} from '../../auth/services/auth-service';
import {Role} from '../../../shared/entities/role';
import {UserBasic} from '../../../shared/entities/user-basic';
import {List} from '../../../core/data-structures/lists/list';
import {Participant} from '../../events/models/participant';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);


  public getAllUsersBasic(): Observable<List<UserBasic>> {
    return this.http.get<UserBasic[]>(`${environment.usersEndpoint}`)
      .pipe(map(eventAdmins => new List<UserBasic>(eventAdmins)));
  }

  updateProfilePicture(formData: FormData) : Observable<LoginResponse> {
    const userId = this.authService.currentUser()?.id;
    if (!userId) {
      return throwError(() => new Error('User must be logged in'));
    }

    return this.http.patch<LoginResponse>(`${environment.apiBaseUrl}/users/${userId}/profile-picture`, formData)
      .pipe(take(1))
      .pipe(
        tap(response => {
          this.authService.updateUserInStorage(response.user);
        })
      );
  }

  // TODO: still needs to be implemented correctly
  updatePassword(password: string): Observable<LoginResponse> {
    return this.http.patch<LoginResponse>(`${environment.apiBaseUrl}/users/${this.authService.currentUser()?.id}`, { password })
      .pipe(take(1));
  }

  public getCountryCodes(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiBaseUrl}/users/countries`);
  }
}
