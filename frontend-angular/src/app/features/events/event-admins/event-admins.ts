import {Component, computed, effect, inject, input, OnInit, signal} from '@angular/core';
import {Chip} from "primeng/chip";
import {MultiSelect} from "primeng/multiselect";
import {PrimeTemplate} from "primeng/api";
import {User} from '../../auth/models/user';
import {Event} from '../models/event';
import {FormsModule} from '@angular/forms';
import {EventService} from '../services/event-service';
import {UserService} from '../../users/services/user-service';
import {UserBasic} from '../../../shared/entities/user-basic';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {ToastService} from '../../../shared/services/toast-service';

@Component({
  selector: 'app-event-admins',
  imports: [
    Chip,
    MultiSelect,
    PrimeTemplate,
    FormsModule
  ],
  templateUrl: './event-admins.html',
  styleUrl: './event-admins.scss',
})
export class EventAdmins implements OnInit {

  private userService = inject(UserService);
  private eventService = inject(EventService);
  private accessControlService = inject(AccessControlService);
  private toastService = inject(ToastService);

  users = signal<UserBasic[]>([]);
  event = input<Event>();
  selectedEventAdmins = signal<User[]>([]);
  loggedInUser = this.accessControlService.getCurrentUser();
  selectableUsers = computed(() =>
    this.users().filter(u => !this.isProtectedFromRemoval(u.id))
  );

  constructor() {
    effect(() => {
      const event = this.event();
      if (!event) return;

      const admins = event.eventAdmins ?? [];
      const creator = event.creator ? [event.creator] : [];
      const currentUser = this.loggedInUser ? [this.loggedInUser] : [];

      const unique = Array.from(
        new Map([...creator, ...currentUser, ...admins].map(u => [u.id, u])).values()
      );

      this.selectedEventAdmins.set(unique);
    });
  }

  ngOnInit(): void {
    this.userService.getAllUsersBasic().subscribe({
      next: (response) => {
        this.users.set(response);
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onEventAdminsChange(newAdmins: User[]) {
    const event = this.event();
    if (!event) return;

    const previousAdmins = this.selectedEventAdmins();
    const prevIds = new Set(previousAdmins.map(u => u.id));
    const nextIds = new Set(newAdmins.map(u => u.id));

    const addedAdmins = newAdmins.filter(u => !prevIds.has(u.id));
    const removedAdmins = previousAdmins.filter(u => !nextIds.has(u.id));

    // prevent removing event creator and logged-in user
    if (!newAdmins.some(u => u.id === event.creator?.id || u.id === this.loggedInUser?.id)) {
      this.toastService.error('You cannot remove the event creator or yourself!');
      this.selectedEventAdmins.set([...newAdmins, event.creator!, this.loggedInUser!]);
      return;
    }

    this.selectedEventAdmins.set(newAdmins);

    if (addedAdmins.length) {
      this.eventService.addEventAdmins(event.id, addedAdmins).subscribe({
        next: (updatedEvent) => {
          console.log('Added event admins successfully', updatedEvent);
        },
        error: (err) => {
          console.error('Failed to add event admins', err);
        }
      });
    }

    if (removedAdmins.length) {
      this.eventService.removeEventAdmins(event.id, removedAdmins).subscribe({
        next: () => {},
        error: (err) => {
          console.error('Failed to remove event admins', err);
        }
      });
    }
  }

  handleChipRemove(user: User) {
    const updated = this.selectedEventAdmins().filter(u => u.id !== user.id);
    this.onEventAdminsChange(updated);
  }

  isProtectedFromRemoval(userId: string): boolean {
    const currentUserId = this.accessControlService.getCurrentUser()?.id;
    const creatorId = this.event()?.creator.id;

    return userId === currentUserId || userId === creatorId;
  }
}
