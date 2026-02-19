import {inject, Injectable} from '@angular/core';
import {User} from '../../features/auth/models/user';
import {Event} from '../../features/events/models/event'
import {LambdaFunctions} from '../utils/lambda-functions';
import {RoleEnum} from '../enums/role-enum';
import {AuthService} from '../../features/auth/services/auth-service';

@Injectable({
  providedIn: 'root',
})
export class AccessControlService {

  authService = inject(AuthService);

  public isCurrentUserEventAdmin(event: Event | null): boolean {
    if (!event || !this.getCurrentUser()) {
      return false;
    }

    return event.eventAdmins.contains(<User>this.getCurrentUser(), LambdaFunctions.compareById);
  }

  public getCurrentUser() {
    return this.authService.currentUser();
  }

  hasAdminAccess(): boolean {
    return this.authService.getUserRoles().some(role => [RoleEnum.ADMIN, RoleEnum.SUPERADMIN].includes(role as RoleEnum));
  }
}
