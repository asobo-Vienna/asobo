import { Injectable } from '@angular/core';
import {User} from '../../features/auth/models/user';
import {Event} from '../../features/events/models/event'
import {LambdaFunctions} from '../utils/lambda-functions';

@Injectable({
  providedIn: 'root',
})
export class AccessControlService {

  public isCurrentUserEventAdmin(event: Event | null, currentUser: User | null): boolean {
    if (!event || !currentUser) {
      return false;
    }

    // console.log('event admin? = ',event.eventAdmins.contains(currentUser, LambdaFunctions.compareById));

    return event.eventAdmins.contains(currentUser, LambdaFunctions.compareById);
  }

}
