import {Routes} from '@angular/router';
import {EventsPage} from './features/events/events-page/events-page';
import {EventDetailPage} from './features/events/event-detail-page/event-detail-page';
import {LoginPage} from './features/auth/login/login-page/login-page';
import {authGuard} from './features/auth/auth.guard';
import {UserProfile} from './features/users/user-profile/user-profile';
import {RegisterPage} from './features/register/register-page/register-page';

export const routes: Routes = [
  // public routes
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'events', component: EventsPage },
  { path: 'events/:id', component: EventDetailPage },

  // everything else needs authentication
  {
    path:'',
    canActivate: [authGuard],
    children: [
      { path: 'user/:username', component: UserProfile },
      { path: '', redirectTo: '/events', pathMatch: 'full' },
    ]
  },

  { path: '**', redirectTo: '/events' } // fallback for unknown routes
];
