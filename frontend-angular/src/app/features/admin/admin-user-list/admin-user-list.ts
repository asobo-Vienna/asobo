import {Component, inject} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { User } from '../../auth/models/user';
import { DatePipe } from '@angular/common';
import {UserService} from '../services/user-service';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-admin-user-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe
  ],
  templateUrl: './admin-user-list.html',
  styleUrl: './admin-user-list.scss',
})
export class AdminUserList {
  private userService = inject(UserService);
  users: User[] = [];

  ngOnInit(): void {
      this.userService.getAllUsers().subscribe({
        next: (users) => {
          this.users = users;
          console.log(this.users);
        },
        error: (err) => console.error('Error fetching users:', err)
      });
      return;
  }

  protected readonly UrlUtilService = UrlUtilService;

  onEdit(user: any) {
    console.log('Editing user:', user);
  }

  onDelete(user: any) {
    console.log('Deleting user:', user);
  }
}

