import {Component, inject} from '@angular/core';
import {AdminUserList} from '../admin-user-list/admin-user-list';
import {AdminEventList} from '../admin-event-list/admin-event-list';
import {AdminCommentList} from '../admin-comment-list/admin-comment-list';
import {AdminMediaList} from '../admin-media-list/admin-media-list';
import {AuthService} from '../../auth/services/auth-service';
import {Role} from '../../../shared/enums/Role';
import {Tab, TabList, TabPanel, TabPanels, Tabs} from 'primeng/tabs';

@Component({
  selector: 'app-admin-page',
  imports: [
    AdminUserList,
    AdminEventList,
    AdminCommentList,
    AdminMediaList,
    Tabs,
    TabList,
    Tab,
    TabPanels,
    TabPanel
  ],
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.scss',
})
export class AdminPage {
  authService = inject(AuthService);
  private _selectedTab: string = "users";
  loadedTabs: Set<string> = new Set(["users"]);

  get selectedTab() {
    return this._selectedTab;
  }

  set selectedTab(tabName: string) {
    this._selectedTab = tabName;
    this.loadedTabs.add(tabName);
  }
}
