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
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);

  private pageCache = new Map<string, User[]>();

  ngOnInit(): void {
    this.loadUsers(0, environment.defaultPageSize);
  }

  loadUsers(page: number, size: number): void {
    const cacheKey = `${page}-${size}`;

    // Check if page is already cached
    if (this.pageCache.has(cacheKey)) {
      this.users.set(this.pageCache.get(cacheKey)!);
      return;
    }

    this.loading.set(true);

    this.adminService.getAllUsers(page, size).subscribe({
      next: (response) => {
        // Cache the page data
        this.pageCache.set(cacheKey, response.content);

        this.users.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching users:', err);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadUsers(page, event.rows);
  }

  // Clear cache when data changes (after edit/delete)
  clearCache(): void {
    this.pageCache.clear();
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;

  onEdit(user: any) {
    console.log('Editing user:', user);
    // After editing, you might want to clear cache
    this.clearCache();
  }

  onDelete(user: any) {
    console.log('Deleting user:', user);
    // After deleting, clear cache and reload
    this.clearCache();
    // stay on page, thus track currently loaded users and update numbers:
    // this.loadUsers(0, 10);
  }

  getUserRouterLink(username: string): string {
    return `${environment.userProfileBaseUrl}${username}`;
  }
}
