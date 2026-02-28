import {Component, computed, inject, input, OnInit, output, signal} from '@angular/core';
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {DatePicker} from "primeng/datepicker";
import {DatePipe} from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {environment} from '../../../../../environments/environment';
import {DateUtils} from '../../../../shared/utils/date/date-utils';
import {Event} from '../../models/event';
import {EventService} from '../../services/event-service';
import {AccessControlService} from '../../../../shared/services/access-control-service';
import {Router} from '@angular/router';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {ToastService} from '../../../../shared/services/toast-service';

@Component({
  selector: 'app-event-basic-info',
  imports: [
    CdkTextareaAutosize,
    DatePicker,
    DatePipe,
    InputText,
    ReactiveFormsModule,
    ToggleSwitch
  ],
  templateUrl: './event-basic-info.html',
  styleUrl: './event-basic-info.scss',
})
export class EventBasicInfo implements OnInit {
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);
  protected accessControlService = inject(AccessControlService);
  private router = inject(Router);
  private toastService = inject(ToastService);

  protected readonly environment = environment;

  // signal inputs
  event = input<Event | null>(null);

  isAdminOrEventAdmin = computed(() => {
    return this.accessControlService.hasAdminAccess() ||
      this.accessControlService.isCurrentUserEventAdmin(this.event());
  });

  isEventInThePast = computed(() => {
    const event = this.event();
    if (!event?.date) return false;

    return DateUtils.isDateInThePast(new Date(event.date));
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
      date: ['', [Validators.required, DateUtils.validateDate]],
      isPrivateEvent: [false, [Validators.required]],
    });
  }

  public ngOnInit(): void {
    const coreInfo = this.event();
    if (coreInfo) {
      this.editEventForm.patchValue({
        title: coreInfo.title,
        description: coreInfo.description,
        location: coreInfo.location,
        date: coreInfo.date ? new Date(coreInfo.date) : null,
        isPrivateEvent: coreInfo.isPrivateEvent
      });
    }
  }

  public onSubmit() {
    if (!this.editEventForm.valid) {
      this.toastService.error('Form is invalid, stopping event submission');
      return;
    }

    const eventData = {
      ...this.editEventForm.value,
      date: DateUtils.toLocalISOString(this.editEventForm.value.date)
    };

    const currentEvent = this.event();
    if (!currentEvent) {
      this.toastService.error('Event not available');
      return;
    }

    this.eventService.updateEvent(currentEvent.id, eventData).subscribe({
      next: (event) => {
        this.toastService.success(`Event ${event.title} updated successfully!`);
        this.eventUpdated.emit(event);
        this.isEditing.set(false);
      },
      error: (err) => {
        this.toastService.error('Error updating event')
        console.log(err);
      }
    });
  }

  public enterEdit() {
    this.isEditing.set(true);
  }

  public cancelEdit() {
    const coreInfo = this.event();
    if (!coreInfo) return;

    this.editEventForm.reset({
      title: coreInfo.title,
      description: coreInfo.description,
      location: coreInfo.location,
      date: coreInfo.date ? new Date(coreInfo.date) : null,
      isPrivateEvent: coreInfo.isPrivateEvent
    });

    this.isEditing.set(false);
  }

  public onDelete() {
    const eventId: string | undefined = this.event()?.id;
    if (!eventId) {
      this.toastService.error('Event with this ID not available');
      return;
    }

    this.eventService.deleteEvent(eventId).subscribe({
      next: () => {
        this.router.navigate(['/events']);
      },
      error: (err) => {
        this.toastService.error('Error deleting event')
        console.error(err);
      }
    });
  }
}
