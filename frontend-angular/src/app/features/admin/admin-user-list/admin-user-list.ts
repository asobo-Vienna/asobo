import {Component, inject, OnInit, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {TagModule} from 'primeng/tag';
import {User} from '../../auth/models/user';
import {DatePipe} from '@angular/common';
import {AdminService} from '../services/admin-service';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {RouterLink} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {MultiSelect} from 'primeng/multiselect';
import {FormsModule} from '@angular/forms';
import {Role} from '../../../shared/entities/role';
import {RoleEnum} from '../../../shared/enums/role-enum';
import {Chip} from 'primeng/chip';
import {UserRoles} from '../../../shared/entities/user-roles';
import {LambdaFunctions} from '../../../shared/utils/lambda-functions';
import {UserFilters} from '../../users/user-profile/models/user-filters';

@Component({
  selector: 'app-admin-user-list',
  imports: [
    TableModule,
    TagModule,
    DatePipe,
    RouterLink,
    MultiSelect,
    FormsModule,
    Chip,
  ],
  templateUrl: './admin-user-list.html',
  styleUrl: './admin-user-list.scss',
})
export class AdminUserList implements OnInit {
  private adminService = inject(AdminService);

  // Role hierarchy definition
  private readonly ROLE_HIERARCHY: { [key in RoleEnum]?: RoleEnum[] } = {
    [RoleEnum.SUPERADMIN]: [RoleEnum.ADMIN, RoleEnum.USER],
    [RoleEnum.ADMIN]: [RoleEnum.USER]
  };

  allRoles = signal<Role[]>([]);
  users = signal<User[]>([]);
  totalRecords = signal<number>(0);
  loading = signal<boolean>(true);
  userFilters = signal<UserFilters>({});

  // Store all user roles in a single signal array
  private userRolesStore = signal<UserRoles[]>([]);

  private pageCache = new Map<string, User[]>();
  private roleCache = new Map<string, Role[]>();
  private sortedRolesCache = new Map<string, Role[]>();
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
    if (this.roleCache.has(rolesKey)) {
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
      this.initializeUserRolesStore(cachedUsers);
      this.loading.set(false);
      return;
    }

    this.adminService.getAllUsers(page, size, this.userFilters()).subscribe({
      next: response => {
        this.pageCache.set(cacheKey, response.content);

        this.users.set(response.content);
        this.totalRecords.set(response.totalElements);
        this.initializeUserRolesStore(response.content);
        this.loading.set(false);
      },
      error: err => {
        console.error('Error fetching users:', err);
        this.loading.set(false);
      }
    });
  }

  private initializeUserRolesStore(users: User[]): void {
    this.sortedRolesCache.clear();
    const userRolesList: UserRoles[] = users.map(user => ({
      userId: user.id,
      roles: [...(user.roles || [])]
    }));
    this.userRolesStore.set(userRolesList);
  }

  getUserRoles(user: User): Role[] {
    const userRoles = this.userRolesStore().find(ur => ur.userId === user.id);
    const roles = userRoles?.roles || [];

    const cacheKey = `${user.id}-${roles.map(r => r.id).join(',')}`;

    if (this.sortedRolesCache.has(cacheKey)) {
      return this.sortedRolesCache.get(cacheKey)!;
    }

    const roleOrder = [RoleEnum.SUPERADMIN, RoleEnum.ADMIN, RoleEnum.USER];
    const sorted = [...roles].sort((a, b) => {
      const indexA = roleOrder.indexOf(a.name as RoleEnum);
      const indexB = roleOrder.indexOf(b.name as RoleEnum);
      return (indexA === -1 ? 999 : indexA) - (indexB === -1 ? 999 : indexB);
    });

    this.sortedRolesCache.set(cacheKey, sorted);
    return sorted;
  }

  private applyRoleHierarchy(newSelection: Role[], currentRoles: Role[]): Role[] {
    const currentNames = new Set(currentRoles.map(r => r.name));
    const newNames = new Set(newSelection.map(r => r.name));

    let changedRole: RoleEnum | undefined;
    let wasAdded = false;

    for (const name of newNames) {
      if (!currentNames.has(name)) {
        changedRole = name as RoleEnum;
        wasAdded = true;
        break;
      }
    }
    if (!changedRole) {
      for (const name of currentNames) {
        if (!newNames.has(name)) {
          changedRole = name as RoleEnum;
          wasAdded = false;
          break;
        }
      }
    }

    if (!changedRole) return newSelection;

    const resultNames = new Set(newNames);

    if (wasAdded) {
      const dependencies = this.ROLE_HIERARCHY[changedRole] ?? [];
      dependencies.forEach(dep => resultNames.add(dep));
    } else {
      resultNames.delete(changedRole);

      for (const [role, deps] of Object.entries(this.ROLE_HIERARCHY)) {
        if (deps?.includes(changedRole)) resultNames.delete(role as RoleEnum);
      }
    }

    return this.allRoles().filter(role => resultNames.has(role.name));
  }

  onRolesChange(newSelection: Role[], user: User): void {
    const currentRoles = this.getUserRoles(user);

    // deselecting the user role should be impossible
    if (!newSelection.find(role => role.name === RoleEnum.USER)) {
      this.updateUserRolesStore(user, currentRoles);
      return;
    }

    const hierarchicalRoles = this.applyRoleHierarchy(newSelection, currentRoles);

    // Update the signal store - create new array to trigger change detection
    this.updateUserRolesStore(user, hierarchicalRoles);

    this.adminService.updateUserRoles(user.id, hierarchicalRoles).subscribe({
      next: () => {
        console.log('Backend updated successfully');
        const cacheKey = `${this.currentPage()}-${this.currentSize()}`;
        if (this.pageCache.has(cacheKey)) {
          const cachedUsers = this.pageCache.get(cacheKey)!;
          const updatedCache = cachedUsers.map(u =>
            u.id === user.id ? {...u, roles: hierarchicalRoles} : u
          );
          this.pageCache.set(cacheKey, updatedCache);
        }

        const userIndex = this.users().findIndex(u => u.id === user.id);
        if (userIndex !== -1) {
          const updatedUsers = [...this.users()];
          updatedUsers[userIndex] = {...updatedUsers[userIndex], roles: hierarchicalRoles};
          this.users.set(updatedUsers);
        }
      },
      error: (err) => {
        console.error('Error updating roles:', err);
        // Revert to previous state
        this.updateUserRolesStore(user, currentRoles);
      }
    });
  }

  handleChipRemove(role: Role, user: User) {
    const currentRoles = this.getUserRoles(user);

    // Remove the clicked role
    const newSelection = currentRoles.filter(r => r.id !== role.id);

    this.onRolesChange(newSelection, user);
  }

  private updateUserRolesStore(user: User, roles: Role[]) {
    const updatedStore = this.userRolesStore().map(ur =>
      ur.userId === user.id
        ? {userId: ur.userId, roles: [...roles]}
        : ur
    );
    this.userRolesStore.set(updatedStore);
  }

  onPageChange(event: any): void {
    const page = event.first / event.rows;
    this.loadUsers(page, event.rows);
  }

  clearCache(): void {
    this.pageCache.clear();
  }

  onEdit(user: any) {
    console.log('Editing user:', user);
    this.clearCache();
  }

  onDelete(user: User) {
    console.log('Deleting user:', user);
    this.adminService.deleteUserById(user.id).subscribe({
      next: () => {
        this.users.update(currentUsers =>
          LambdaFunctions.removeById(currentUsers, user.id)
        );
      },
      error: (err) => console.log('Error deleting user:', err),
    });

    this.clearCache();
  }

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;
}
