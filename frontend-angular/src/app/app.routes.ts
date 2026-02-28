import {Routes} from '@angular/router';
import {EventList} from './features/events/event-list/event-list';
import {EventDetailPage} from './features/events/event-detail-page/event-detail-page';
import {LoginPage} from './features/auth/login/login-page/login-page';
import {authGuard} from './features/auth/auth.guard';
import {RegistrationPage} from './features/auth/registration/registration-page/registration-page';
import {AboutPage} from './core/about/about-page/about-page';
import {UserProfilePage} from './features/users/user-profile/user-profile-page/user-profile-page';
import {CreateEventPage} from './features/events/create-event/create-event-page/create-event-page';
import {AdminPage} from './features/admin/admin-page/admin-page';
import {SearchResultsPage} from './features/search/search-results/search-results-page';
import {eventDetailResolver} from './features/events/event-detail-page/event-detail.resolver';
import {userProfileResolver} from './features/users/user-profile/user-profile.resolver';

export const routes: Routes = [
  // public routes
  {path: 'about', component: AboutPage, title: 'About – asobō'},
  {path: 'login', component: LoginPage, title: 'Login – asobō'},
  {path: 'register', component: RegistrationPage, title: 'Registration – asobō'},
  {path: 'events', component: EventList, title: 'Events – asobō'},
  {path: 'events/:id', component: EventDetailPage, resolve: {event: eventDetailResolver}},
  {path: '', component: LoginPage, title: 'Login – asobō'},

  // everything else needs authentication
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {path: 'user/:username', component: UserProfilePage, resolve: {user: userProfileResolver}},
      {path: 'events', component: EventList, title: 'Events – asobō'},
      {path: 'create-event', component: CreateEventPage, title: 'Create New Event – asobō'},
      {path: 'admin', component: AdminPage, title: 'Admin Section – asobō'},
      {path: 'search', component: SearchResultsPage, title: 'Search Results – asobō'}
      //{ path: '', redirectTo: '/events', pathMatch: 'full' , title: 'Events – asobō' },
      //{ path: '', redirectTo: '/login', pathMatch: 'full', title: 'Login – asobō' },
    ]
  },

  {path: '**', redirectTo: '/events', title: 'Events – asobō'} // fallback for unknown routes
];
