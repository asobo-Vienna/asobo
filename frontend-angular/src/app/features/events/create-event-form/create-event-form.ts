import {Component, inject, signal} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators} from "@angular/forms";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import {DatePickerModule} from 'primeng/datepicker';
import { ProfilePictureUpload } from '../../users/profile-picture-upload/profile-picture-upload';

@Component({
  selector: 'app-create-event-form',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    DatePickerModule,
    ProfilePictureUpload,
  ],
  templateUrl: './create-event-form.html',
  styleUrl: './create-event-form.scss',
})
export class CreateEventForm {
  createEventForm: FormGroup;
  private formBuilder = inject(FormBuilder);
  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;

  constructor() {
    this.createEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      location: ['', [Validators.required]],
      date: [new Date(), [Validators.required, this.validateDate]],
    });
  }

  onSubmit() {
    console.log(this.createEventForm.getRawValue());
    console.log(this.selectedImage);
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
}
