import {Component, inject, OnInit, signal} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { User } from '../../auth/models/user';
import { DatePipe } from '@angular/common';
import {AdminService} from '../services/admin-service';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {RouterLink} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {MultiSelect} from 'primeng/multiselect';

@Component({
  selector: 'app-admin-user-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
    RouterLink,
    MultiSelect
  ],
  templateUrl: './admin-user-list.html',
  styleUrl: './admin-user-list.scss',
})
export class AdminUserList implements OnInit {
  private adminService = inject(AdminService);
  users = signal<User[]>([]);
  loading = true;

  ngOnInit(): void {
      this.adminService.getAllUsers().subscribe({
        next: (users) => {
          this.users.set(users);
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching users:', err);
          this.loading = false;
        }
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

  getUserRouterLink(username: string): string {
    return `${environment.userProfileBaseUrl}${username}`;
  }

  protected readonly environment = environment;
}

