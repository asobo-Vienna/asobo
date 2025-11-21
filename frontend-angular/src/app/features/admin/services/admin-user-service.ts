import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {User} from '../../auth/models/user';

@Injectable({
  providedIn: 'root',
})
export class AdminUserService {
  private http = inject(HttpClient);

  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiBaseUrl}/admin/users`);
  }
}
