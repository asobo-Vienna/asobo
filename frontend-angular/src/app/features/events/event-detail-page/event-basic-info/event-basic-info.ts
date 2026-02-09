import {Component, computed, inject, input, OnInit, output, signal} from '@angular/core';
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {DatePicker} from "primeng/datepicker";
import {DatePipe} from "@angular/common";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {environment} from '../../../../../environments/environment';
import {DateUtils} from '../../../../shared/utils/date/date-utils';
import {Event} from '../../models/event';
import {User} from '../../../auth/models/user';
import {EventCoreInfo} from '../../models/event-core-info';
import {EventService} from '../../services/event-service';
import {AuthService} from '../../../auth/services/auth-service';
import {AccessControlService} from '../../../../shared/services/access-control-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-event-basic-info',
  imports: [
    CdkTextareaAutosize,
    DatePicker,
    DatePipe,
    FormsModule,
    InputText,
    ReactiveFormsModule
  ],
  templateUrl: './event-basic-info.html',
  styleUrl: './event-basic-info.scss',
})
export class EventBasicInfo implements OnInit {
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);
  protected accessControlService = inject(AccessControlService);
  protected authService = inject(AuthService);
  private router = inject(Router);

  protected readonly environment = environment;

  // signal inputs
  eventCoreInfo = input<EventCoreInfo | null>(null);
  event = input<Event | null>(null);
  currentUser = input<User | null>(null);

  isAdminOrEventAdmin = computed(() => {
    return this.authService.hasAdminAccess() ||
      this.accessControlService.isCurrentUserEventAdmin(this.event(), this.currentUser());
  });

  // signal output
  eventUpdated = output<Event>();

  editEventForm: FormGroup;
  isEditing = signal(false);


  constructor() {
    this.editEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      date: ['', [Validators.required, this.validateDate]],
      isPrivate: [false, [Validators.required]],
    });
  }

  public ngOnInit(): void {
    const coreInfo = this.eventCoreInfo();
    if (coreInfo) {
      this.editEventForm.patchValue({
        title: coreInfo.title,
        description: coreInfo.description,
        location: coreInfo.location,
        date: coreInfo.date ? new Date(coreInfo.date) : null,
        isPrivate: coreInfo.isPrivate
      });
    }
  }

  public onSubmit() {
    if (!this.editEventForm.valid) {
      console.log('Form is invalid, stopping event submission');
      return;
    }

    const eventData = {
      ...this.editEventForm.value,
      date: DateUtils.toLocalISOString(this.editEventForm.value.date)
    };

    const currentEvent = this.event();
    if (!currentEvent) {
      console.error('Event not available');
      return;
    }

    this.eventService.updateEvent(currentEvent.id, eventData).subscribe({
      next: (event) => {
        console.log(`Event ${event.title} updated successfully!`);
        this.eventUpdated.emit(event);
        this.isEditing.set(false);
      },
      error: (err) => {
        console.log('Error updating event', err);
      }
    });
  }


  public enterEdit() {
    this.isEditing.set(true);
  }

  public cancelEdit() {
    const coreInfo = this.eventCoreInfo();
    if (!coreInfo) return;

    this.editEventForm.reset({
      title: coreInfo.title,
      description: coreInfo.description,
      location: coreInfo.location,
      date: coreInfo.date ? new Date(coreInfo.date) : null,
      isPrivate: coreInfo.isPrivate
    });

    this.isEditing.set(false);
  }

  public validateDate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const selected = new Date(control.value);
    if (selected < new Date()) {
      return { pastDate: true };
    }
    return null;
  }

  public onDelete() {
    const eventId: string | undefined = this.event()?.id;
    if (!eventId) {
      console.error('No event ID available');
      return;
    }

    this.eventService.deleteEvent(eventId).subscribe({
      next: () => {
        this.router.navigate(['/events']);
      },
      error: (err) => {
        console.error('Failed to delete event', err);
      }
    });
  }
}
