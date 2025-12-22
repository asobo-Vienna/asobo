import {Component, inject, OnInit, signal, computed} from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { User } from '../../auth/models/user';
import { DatePipe } from '@angular/common';
import {AdminService} from '../services/admin-service';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {RouterLink} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {MultiSelect} from 'primeng/multiselect';
import {Role} from '../../../shared/enums/Role';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-admin-user-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
    RouterLink,
    MultiSelect,
    FormsModule
  ],
  templateUrl: './admin-user-list.html',
  styleUrl: './admin-user-list.scss',
})
export class AdminUserList implements OnInit {
  private adminService = inject(AdminService);

  allRoles = signal<Role[]>([]);
  users = signal<User[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);

  userRolesMap = signal<Map<string, Role[]>>(new Map());

  private pageCache = new Map<string, User[]>();
  private roleCache = new Map<string, Role[]>();
  private currentPage = signal<number>(0);
  private currentSize = signal<number>(environment.defaultPageSize);

  ngOnInit(): void {
    this.loadUsers(0, environment.defaultPageSize);
  }

  loadUsers(page: number, size: number): void {
    const cacheKey = `${page}-${size}`;
    const rolesKey = 'allRoles';

    this.loading.set(true);
    this.currentPage.set(page);
    this.currentSize.set(size);

    // Load roles if not cached
    if(this.roleCache.has(rolesKey)) {
      this.allRoles.set(this.roleCache.get(rolesKey)!);
    } else {
      this.adminService.getAllRoles().subscribe({
        next: response => {
          this.roleCache.set(rolesKey, response);
          this.allRoles.set(response);
        },
        error: err => {
          console.error('Error fetching roles:', err);
          this.loading.set(false);
        }
      });
    }

    if (this.pageCache.has(cacheKey)) {
      const cachedUsers = this.pageCache.get(cacheKey)!;
      this.users.set(cachedUsers);
      this.initializeUserRolesMap(cachedUsers);
      this.loading.set(false);
      return;
    }

    this.adminService.getAllUsers(page, size).subscribe({
      next: response => {
        this.pageCache.set(cacheKey, response.content);

        this.users.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.initializeUserRolesMap(response.content);
        this.loading.set(false);
      },
      error: err => {
        console.error('Error fetching users:', err);
        this.loading.set(false);
      }
    });
  }

  private initializeUserRolesMap(users: User[]): void {
    const rolesMap = new Map<string, Role[]>();
    users.forEach(user => {
      rolesMap.set(user.id, user.roles! || []);
    });
    this.userRolesMap.set(rolesMap);
  }

  getUserRoles(user: User): Role[] {
    return this.userRolesMap().get(user.id) || user.roles || [];
  }

  onRolesChange(selectedRoles: Role[], user: User): void {
    const previousRoles = this.getUserRoles(user);

    const currentMap = new Map(this.userRolesMap());
    currentMap.set(user.id, selectedRoles);
    this.userRolesMap.set(currentMap);

    // Update backend (don't update signals here to avoid re-render/close)
    this.adminService.updateUserRoles(user.id, selectedRoles).subscribe({
      next: (response) => {
        console.log('Roles updated successfully:', response);

        const cacheKey = `${this.currentPage()}-${this.currentSize()}`;
        if (this.pageCache.has(cacheKey)) {
          const cachedUsers = this.pageCache.get(cacheKey)!;
          const updatedCache = cachedUsers.map(u =>
            u.id === user.id ? { ...u, roles: selectedRoles as any } : u
          );
          this.pageCache.set(cacheKey, updatedCache);
        }
      },
      error: (err) => {
        console.error('Error updating roles:', err);

        // Revert on error
        const revertMap = new Map(this.userRolesMap());
        revertMap.set(user.id, previousRoles);
        this.userRolesMap.set(revertMap);
      }
    });
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadUsers(page, event.rows);
  }

  clearCache(): void {
    this.pageCache.clear();
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;

  onEdit(user: any) {
    console.log('Editing user:', user);
    this.clearCache();
  }

  onDelete(user: any) {
    console.log('Deleting user:', user);
    this.clearCache();
  }

  getUserRouterLink(username: string): string {
    return `${environment.userProfileBaseUrl}${username}`;
  }
}
