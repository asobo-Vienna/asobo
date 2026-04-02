import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {DatePickerModule} from 'primeng/datepicker';
import {PictureUpload} from '../../../../core/picture-upload/picture-upload';
import {EventService} from '../../services/event-service';
import {AuthService} from '../../../auth/services/auth-service';
import {Router} from '@angular/router';
import {environment} from '../../../../../environments/environment';
import {Textarea} from 'primeng/textarea';
import {Checkbox} from 'primeng/checkbox';
import {DateUtils} from '../../../../shared/utils/date/date-utils';
import {ToastService} from '../../../../shared/services/toast-service';
import {Select} from 'primeng/select';
import {EventCategory} from '../../../../shared/enums/event-category';

@Component({
  selector: 'app-create-event-form',
  imports: [
    ReactiveFormsModule,
    DatePickerModule,
    PictureUpload,
    Textarea,
    Checkbox,
    Select,
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
  private toastService = inject(ToastService);
  previewUrl = signal<string | null>(null);
  selectedImage: File | null = null;

  eventCategories = Object.values(EventCategory);

  protected readonly environment = environment;

  constructor() {
    const today = new Date();
    today.setMinutes(today.getMinutes() + 30);

    this.createEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      category: ['', [Validators.required]],
      date: [today, [Validators.required, DateUtils.validateDate]],
      isPrivateEvent: [false]
    });
  }

  onSubmit() {
    if (!this.createEventForm.valid) {
      const errMsg: string = 'Form is invalid, stopping event submission';
      console.log(errMsg);
      this.toastService.error(errMsg);
      return;
    }

    const eventData = {
      ...this.createEventForm.value,
      date: DateUtils.toLocalISOString(this.createEventForm.value.date)
    };

    const creator = this.authService.currentUser();
    if (creator) {
      eventData.creator = {id: creator.id};
    }

    this.eventService.createNewEvent(eventData).subscribe({
      next: (event) => {
        const successMsg: string = `Event ${event.title} created successfully!`;
        console.log(successMsg);
        this.toastService.success(successMsg);

        if (this.selectedImage) {
          this.uploadEventPicture(event.id);
        } else {
          this.router.navigate(['/events', event.id]);
        }
      },
      error: (err) => {
        const errMsg: string = 'Error creating new event';
        console.log(errMsg, err);
        this.toastService.error(errMsg);
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
        const errMsg: string = 'Error uploading event picture';
        console.log(errMsg, err);
        this.toastService.error(errMsg);
      }
    });
  }

  handleFileSelected(file: File) {
    this.selectedImage = file;
  }

  get getFormControls() {
    return this.createEventForm.controls;
  }
}
