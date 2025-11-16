import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from "@angular/forms";
import { DatePickerModule } from 'primeng/datepicker';
import { PictureUpload } from '../../../../core/picture-upload/picture-upload';
import { EventService } from '../../services/event-service';
import { AuthService } from '../../../auth/services/auth-service';
import { Router } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { Textarea } from 'primeng/textarea';
import { Checkbox } from 'primeng/checkbox';

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
  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;

  constructor() {
    this.createEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      date: [new Date(), [Validators.required, this.validateDate]],
      isPrivate: [false]
    });
  }

  onSubmit() {
    const formData = new FormData();
    formData.append('title', this.createEventForm.value.title!);
    formData.append('description', this.createEventForm.value.description!);
    formData.append('location', this.createEventForm.value.location!);

    const date: Date = this.createEventForm.value.date;
    const isoLocal = date.toISOString().slice(0, 19); // '2025-11-13T16:33:28'
    formData.append('date', isoLocal);

    const isPrivate = this.createEventForm.value.isPrivate;
    formData.append('private', String(isPrivate));

    const creator = this.authService.currentUser();
    if (creator) {
      formData.append('creator.id', creator.id);
    }

    if (this.selectedImage) {
      formData.append('eventPicture', this.selectedImage);
    }

    this.eventService.createNewEvent(formData).subscribe({
      next: (event) => {
        console.log(event);
        this.router.navigate(['/events', event.id]);
      },
      error: (err) => {
        console.log('Error creating new event', err);
      }
    });
  }

  handleFileSelected(file: File) {
    this.selectedImage = file;
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
    return this.createEventForm.controls;
  }

  protected readonly environment = environment;
}
