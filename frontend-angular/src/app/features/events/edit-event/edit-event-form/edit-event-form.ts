import {Component, input, inject, output, effect} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { DatePickerModule } from 'primeng/datepicker';
import {Checkbox} from 'primeng/checkbox';
import {Textarea} from 'primeng/textarea';
import {Router} from '@angular/router';
import {EventService} from '../../services/event-service';
import {AuthService} from '../../../auth/services/auth-service';
import {environment} from '../../../../../environments/environment';
import {DateUtils} from '../../../../shared/utils/date/date-utils';

@Component({
  selector: 'app-edit-event-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    DatePickerModule,
    Checkbox,
    Textarea
  ],
  templateUrl: './edit-event-form.html',
  styleUrl: './edit-event-form.scss',
})
export class EditEventForm {
  private router = inject(Router);
  editEventForm: FormGroup;
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);
  private authService = inject(AuthService);

  id = input<string>('');
  title = input<string>('');
  location = input<string>('');
  description = input<string>('');
  date = input<string>('');
  time = input<string>('');
  isPrivate = input<boolean>(false);
  enabled = input<boolean>(false);
  done = output<void>();

  selectedImage: File | null = null;

  constructor() {
    const today = new Date();
    today.setMinutes(today.getMinutes() + 30);

    this.editEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      date: ['', [Validators.required, this.validateDate]],
      isPrivate: ['', [Validators.required]],
    });

    effect(() => {
      if (this.enabled()) {
        this.editEventForm.enable();
      } else {
        this.editEventForm.disable();
      }
    });

  }

  ngOnInit() {
    this.editEventForm.patchValue({
      title: this.title(),
      description: this.description(),
      location: this.location(),
      date: this.date() ? new Date(this.date()) : null,
      isPrivate: this.isPrivate()
    });
  }

  onSubmit() {
    if (!this.editEventForm.valid) {
      console.log('Form is invalid, stopping event submission');
      return;
    }

    const eventData = {
      ...this.editEventForm.value,
      date: DateUtils.toLocalISOString(this.editEventForm.value.date)
    };

    this.eventService.updateEvent(this.id(), eventData).subscribe({
      next: (event) => {
        alert(`Event ${event.title} updated successfully!`);

        if (this.selectedImage) {
          this.uploadEventPicture(event.id);
        } else {
          console.log('About to navigate to:', ['/events', event.id]);
          this.done.emit();
          this.router.navigateByUrl(`/events/${event.id}`);
        }
      },
      error: (err) => {
        console.log('Error updating event', err);
      }
    });
  }

  private uploadEventPicture(eventId: string) {
    const formData = new FormData();
    formData.append('eventPicture', this.selectedImage!);

    this.eventService.uploadEventPicture(eventId, formData).subscribe({
      next: () => {
        console.log('Event picture uploaded successfully!');
        this.router.navigate(['/events', eventId]);
      },
      error: (err) => {
        console.log('Error uploading event picture', err);
      }
    });
  }

  validateDate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const selected = new Date(control.value);
    if (selected < new Date()) {
      return { pastDate: true };
    }
    return null;
  }

  get getFormControls() {
    return this.editEventForm.controls;
  }

  protected readonly environment = environment;
}
