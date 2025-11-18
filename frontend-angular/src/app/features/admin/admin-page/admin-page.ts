import { Component } from '@angular/core';
import {AdminUserList} from '../admin-user-list/admin-user-list';
import {AdminEventList} from '../admin-event-list/admin-event-list';
import {AdminCommentList} from '../admin-comment-list/admin-comment-list';
import {AdminMediaList} from '../admin-media-list/admin-media-list';

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

}
