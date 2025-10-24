import {Routes} from '@angular/router';
import {EventsPage} from './features/events/events-page/events-page';
import {EventDetailPage} from './features/events/event-detail-page/event-detail-page';
import {LoginPage} from './features/auth/login/login-page/login-page';
import {HomePage} from './core/home/home-page/home-page';
import {authGuard} from './features/auth/auth.guard';
import {UserProfile} from './features/users/user-profile/user-profile';
import {RegistrationPage} from './features/auth/registration/registration-page/registration-page';
import {AboutPage} from './core/about/about-page/about-page';

export const routes: Routes = [
  // public routes
  { path: 'about', component: AboutPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegistrationPage },
  { path: 'events/:id', component: EventDetailPage },
  { path: '', component: HomePage },

  // everything else needs authentication
  {
    path:'',
    canActivate: [authGuard],
    children: [
      { path: 'user/:username', component: UserProfile },
      { path: 'events', component: EventsPage },
      //{ path: '', redirectTo: '/events', pathMatch: 'full' },
    ]
  },

  { path: '**', redirectTo: '/events' } // fallback for unknown routes
];
