import {Component, inject, OnInit, signal} from '@angular/core';
import {UserProfileForm} from '../user-profile-form/user-profile-form';
import {ActivatedRoute} from '@angular/router';
import {EventList} from '../../../events/event-list/event-list';
import {List} from '../../../../core/data_structures/lists/list';
import {Event} from '../../../events/models/event';
import {EventService} from '../../../events/services/event-service';

@Component({
  selector: 'app-user-profile-page',
  imports: [
    UserProfileForm,
    EventList
  ],
  templateUrl: './user-profile-page.html',
  styleUrl: './user-profile-page.scss',
})
export class UserProfilePage implements OnInit {
  private route = inject(ActivatedRoute);
  private eventService = inject(EventService);
  profileUsername: string | undefined;
  events = signal<List<Event>>(new List<Event>());
  private userId: string = "";

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.profileUsername = params.get('username')!;
    });
  }

  handleUserIdMessage(userId: string) {
    this.userId = userId;
    this.fetchEvents(); // fetch after receiving userId
  }

  private fetchEvents() {
    if (!this.userId) {
      console.warn('No userId available yet!');
      return;
    }

    console.log("Fetching events for userId:", this.userId);
    this.eventService.getPublicEventsByUserId(this.userId).subscribe({
      next: (eventsArray) => {
        this.events.set(new List<Event>(eventsArray));
      },
      error: (err) => console.error('Error fetching events:', err)
    });
  }
}
