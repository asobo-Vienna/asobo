import {Component, inject, OnInit, signal} from '@angular/core';
import {UserProfileForm} from '../user-profile-form/user-profile-form';
import {ActivatedRoute} from '@angular/router';
import {EventList} from '../../../events/event-list/event-list';
import {List} from '../../../../core/data-structures/lists/list';
import {EventService} from '../../../events/services/event-service';
import {EventSummary} from '../../../events/models/event-summary';
import {Title} from '@angular/platform-browser';
import {Spinner} from '../../../../core/ui-elements/spinner/spinner';
import {Tab, TabList, TabPanel, TabPanels, Tabs} from 'primeng/tabs';

@Component({
  selector: 'app-user-profile-page',
  imports: [
    UserProfileForm,
    EventList,
    Spinner,
    TabPanel,
    TabList,
    TabPanels,
    Tab,
    Tabs
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
  administeredEvents = signal<List<EventSummary>>(new List<EventSummary>());
  loading = signal<boolean>(true);
  selectedTab = signal<string>('attended');
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
    this.fetchEvents(true)
  }

  private fetchEvents(administered: boolean = false) {
    if (!this.userId) {
      console.warn('No userId available yet!');
      return;
    }

    const params = {
      page: 0,
      size: 100,
    };

    if (administered) {
      this.eventService.getEventsPaginated(params, {eventAdminIds: new List<string>([this.userId])}).subscribe({
        next: (response) => {
          this.administeredEvents.set(new List<EventSummary>(response.content));
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Error fetching administered events:', err);
          this.loading.set(false);
        }
      })
      return;
    }

    console.log("Fetching events for userId:", this.userId);
    this.eventService.getEventsPaginated(params, {userId: this.userId}).subscribe({
      next: (response) => {
        this.events.set(new List<EventSummary>(response.content));
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching events:', err);
        this.loading.set(false);
      }
    });
  }
}
