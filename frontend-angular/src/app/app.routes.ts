import {RouterModule, Routes} from '@angular/router';
import { EventsPage } from './domains/events/events-page/events-page';
import {NgModule} from '@angular/core';

export const routes: Routes = [
  { path: 'events', component: EventsPage },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '**', redirectTo: '/' } // fallback for unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutesModule {}
