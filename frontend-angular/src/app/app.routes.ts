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

export const routes: Routes = [
  // public routes
  { path: 'about', component: AboutPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegistrationPage },
  { path: 'events', component: EventList },
  { path: 'events/:id', component: EventDetailPage },
  { path: '', component: LoginPage },

  // everything else needs authentication
  {
    path:'',
    canActivate: [authGuard],
    children: [
      { path: 'user/:username', component: UserProfilePage },
      { path: 'events', component: EventList },
      { path: 'create-event', component: CreateEventPage },
      { path: 'admin', component: AdminPage },
      //{ path: '', redirectTo: '/events', pathMatch: 'full' },
      //{ path: '', redirectTo: '/login', pathMatch: 'full' },
    ]
  },

  { path: '**', redirectTo: '/events' } // fallback for unknown routes
];
