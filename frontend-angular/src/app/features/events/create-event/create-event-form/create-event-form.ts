import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { DatePickerModule } from 'primeng/datepicker';
import { PictureUpload } from '../../../../core/picture-upload/picture-upload';
import { EventService } from '../../services/event-service';
import { AuthService } from '../../../auth/services/auth-service';
import { Router } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { Textarea } from 'primeng/textarea';
import { Checkbox } from 'primeng/checkbox';
import { DateUtils } from '../../../../shared/utils/date/date-utils';

@Component({
  selector: 'app-create-event-form',
  imports: [
    ReactiveFormsModule,
    DatePickerModule,
    PictureUpload,
    Textarea,
    Checkbox,
  ],
  templateUrl: './create-event-form.html',
  styleUrl: './create-event-form.scss',
})
export class CreateEventForm {
  private router = inject(Router);
  createEventForm: FormGroup;
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);
  private authService = inject(AuthService);
  previewUrl = signal<string | null>(null);
  selectedImage: File | null = null;

  constructor() {
    const today = new Date();
    today.setMinutes(today.getMinutes() + 30);

    this.createEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      date: [today, [Validators.required, DateUtils.validateDate]],
      isPrivateEvent: [false]
    });
  }

  onSubmit() {
    if (!this.createEventForm.valid) {
      console.log('Form is invalid, stopping event submission');
      return;
    }

    const eventData = {
      ...this.createEventForm.value,
      date: DateUtils.toLocalISOString(this.createEventForm.value.date)
    };

    const creator = this.authService.currentUser();
    if (creator) {
      eventData.creator = creator;
    }

    this.eventService.createNewEvent(eventData).subscribe({
      next: (event) => {
        console.log(`Event ${event.title} created!`);

        if (this.selectedImage) {
          this.uploadEventPicture(event.id);
        } else {
          this.router.navigate(['/events', event.id]);
        }
      },
      error: (err) => {
        console.log('Error creating new event', err);
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

  handleFileSelected(file: File) {
    this.selectedImage = file;
  }

  get getFormControls() {
    return this.createEventForm.controls;
  }

  protected readonly environment = environment;
}
