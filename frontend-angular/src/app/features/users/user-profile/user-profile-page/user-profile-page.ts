import {Component, inject, OnInit, signal} from '@angular/core';
import {UserProfileForm} from '../user-profile-form/user-profile-form';
import {ActivatedRoute} from '@angular/router';
import {EventList} from '../../../events/event-list/event-list';
import {List} from '../../../../core/data-structures/lists/list';
import {Event} from '../../../events/models/event';
import {EventService} from '../../../events/services/event-service';
import {EventSummary} from '../../../events/models/event-summary';
import {Title} from '@angular/platform-browser';

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
  private titleService = inject(Title);
  private eventService = inject(EventService);
  profileUsername: string | undefined;
  events = signal<List<EventSummary>>(new List<EventSummary>());
  private userId: string = "";

  ngOnInit(): void {
    this.route.data.subscribe(({user}) => {
      this.profileUsername = user.username;
      this.titleService.setTitle(`${user.username} – asobō`);
    });
  }

  handleUserIdMessage(userId: string) {
    this.userId = userId;
    this.fetchEvents();
  }

  private fetchEvents() {
    if (!this.userId) {
      console.warn('No userId available yet!');
      return;
    }

    const params = {
      page: 0,
      size: 100,
    };

    console.log("Fetching events for userId:", this.userId);
    this.eventService.getEventsByUserIdPaginated(this.userId, params, {}).subscribe({
      next: (response) => {
        this.events.set(new List<EventSummary>(response.content));
      },
      error: (err) => console.error('Error fetching events:', err)
    });
  }
}
