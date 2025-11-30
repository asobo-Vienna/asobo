import {Component, inject} from '@angular/core';
import {AdminUserList} from '../admin-user-list/admin-user-list';
import {AdminEventList} from '../admin-event-list/admin-event-list';
import {AdminCommentList} from '../admin-comment-list/admin-comment-list';
import {AdminMediaList} from '../admin-media-list/admin-media-list';
import {AuthService} from '../../auth/services/auth-service';
import {Role} from '../../../shared/enums/Role';

@Component({
  selector: 'app-admin-page',
  imports: [
    AdminUserList,
    AdminEventList,
    AdminCommentList,
    AdminMediaList
  ],
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.scss',
})
export class AdminPage {
  authService = inject(AuthService);
}
