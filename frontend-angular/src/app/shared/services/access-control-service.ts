import {inject, Injectable} from '@angular/core';
import {User} from '../../features/auth/models/user';
import {Event} from '../../features/events/models/event'
import {LambdaFunctions} from '../utils/lambda-functions';
import {RoleEnum} from '../enums/role-enum';
import {AuthService} from '../../features/auth/services/auth-service';
import {EventSummary} from '../../features/events/models/event-summary';

@Injectable({
  providedIn: 'root',
})
export class AccessControlService {

  authService = inject(AuthService);

  public isCurrentUserEventAdmin(event: Event | EventSummary | null): boolean {
    if (!event || !this.getCurrentUser()) {
      return false;
    }

    if ('eventAdmins' in event) {
      return event.eventAdmins.some(a => a.id === this.getCurrentUser()?.id);
    }

    if ('eventAdminIds' in event) {
      return event.eventAdminIds.some(eventAdminId => eventAdminId === this.getCurrentUser()!.id);
    }

    return false;
  }

  public getCurrentUser() {
    return this.authService.currentUser();
  }

  hasAdminAccess(): boolean {
    return this.authService.getUserRoles().some(role => [RoleEnum.ADMIN, RoleEnum.SUPERADMIN].includes(role as RoleEnum));
  }
}
