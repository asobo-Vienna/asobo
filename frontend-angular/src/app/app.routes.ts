import {Routes} from '@angular/router';
import {EventsPage} from './features/events/events-page/events-page';
import {EventDetailPage} from './features/events/event-detail-page/event-detail-page';
import {LoginPage} from './features/auth/login/login-page/login-page';
import {HomePage} from './core/home/home-page/home-page';
import {authGuard} from './features/auth/auth.guard';
import {UserProfileForm} from './features/users/user-profile/user-profile-form/user-profile-form';
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
  { path: 'events/:id', component: EventDetailPage },
  { path: '', component: LoginPage },

  // everything else needs authentication
  {
    path:'',
    canActivate: [authGuard],
    children: [
      { path: 'user/:username', component: UserProfilePage },
      { path: 'events', component: EventsPage },
      { path: 'create-event', component: CreateEventPage },
      { path: 'admin', component: AdminPage },
      //{ path: '', redirectTo: '/events', pathMatch: 'full' },
      //{ path: '', redirectTo: '/login', pathMatch: 'full' },
    ]
  },

  { path: '**', redirectTo: '/events' } // fallback for unknown routes
];
