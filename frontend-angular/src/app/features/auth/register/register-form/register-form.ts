import {Component, ViewEncapsulation, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../auth-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {SelectModule} from 'primeng/select';
import {FormUtilService} from '../../../../shared/utils/form/form-util-service';
import {debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs';

@Component({
  selector: 'app-register-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    SelectModule,
  ],
  templateUrl: './register-form.html',
  styleUrl: './register-form.scss',
  encapsulation: ViewEncapsulation.None,
})
export class RegisterForm {
  registerForm: FormGroup;
  salutations: string[];
  showCustomSalutation: boolean;
  usernameExists: boolean;
  emailExists: boolean;

  private formBuilder = inject(FormBuilder);
  public authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;

  constructor() {
    this.salutations = ['Mr.', 'Ms.', 'Other'];
    this.showCustomSalutation = false;
    this.usernameExists = false;
    this.emailExists = false;

    this.registerForm = this.formBuilder.group({
      salutation: ['',
        Validators.required
      ],
      customSalutation: [''],
      firstName: ['', [
        Validators.required,
      ]],
      surname: ['', [
        Validators.required,
      ]],
      username: ['', [
        Validators.required,
        Validators.minLength(3),
      ]],
      email: ['', [
        Validators.required,
        FormUtilService.strictEmailValidator,
      ]],
      location: ['', [
        Validators.required,
      ]],
      password: ['', [
        Validators.required,
      ]],
      passwordConfirmation: ['', [
        Validators.required,
      ]],
    });

    this.registerForm.get('salutation')?.valueChanges.subscribe(value => {
      this.showCustomSalutation = value === this.salutations[this.salutations.length-1];
      const customSalutationControl = this.registerForm.get('customSalutation');

      if (this.showCustomSalutation) {
        customSalutationControl?.setValidators([Validators.required]);
      } else {
        customSalutationControl?.clearValidators();
        customSalutationControl?.setValue('');
      }

      customSalutationControl?.updateValueAndValidity();
    });

    // Check username availability while typing
    this.registerForm.get('username')?.valueChanges
      .pipe(
        filter(username => username.length >= 3),
        debounceTime(500), // Wait 800ms after typing stops
        distinctUntilChanged(), // Only if value actually changed
        switchMap(username => this.authService.checkUsernameAvailability(username))
      )
      .subscribe(isAvailable => {
        this.usernameExists = !isAvailable;
      });

    // Check email availability while typing
    this.registerForm.get('email')?.valueChanges
      .pipe(
        filter(() => this.registerForm.get('email')?.valid === true), // Only if valid
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(email => this.authService.checkEmailAvailability(email))
      )
      .subscribe(isAvailable => {
        this.emailExists = !isAvailable;
      });
  }

  onProfileBoxClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (target.id !== 'profile-pic-input') {
      const input = document.getElementById('profile-pic-input') as HTMLInputElement;
      input.click();
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    if (!file.type.startsWith('image/')) {
      alert('Please select an image.');
      return;
    }

    this.selectedImage = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl.set(reader.result);

      const box = document.getElementById('profile-picture-box') as HTMLElement;
      if (box) {
        box.style.border = 'none';
        box.style.backgroundColor = 'transparent';
      }
    };
    reader.readAsDataURL(file);
  }



  onSubmit() {
    if (this.registerForm.invalid || this.usernameExists || this.emailExists) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const formDataObj = this.getSubmitData();
    if(!formDataObj) {
      return;
    }
    console.log('Form submitted:', formDataObj);

    const formData = new FormData();

    Object.entries(formDataObj).forEach(([key, value]) => {
      formData.append(key, value as string);
    });

    if (this.selectedImage) {
      formData.append('profilePicture', this.selectedImage);
    }

    this.authService.register(formData).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.usernameExists = false;
        this.emailExists = false;
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigate([returnUrl]);
      },
      error: (err) => {
        console.error('Registration failed with status code:', err.status);
        // TODO: Show error message to user
        if (err.status === 409) {
          console.log("409 conflict");
          const errorResponse = err.error;
          if (errorResponse.code === 'EMAIL_EXISTS') {
            console.error('Email already exists');
            this.emailExists = true;
          } else if (errorResponse.code === 'USERNAME_EXISTS') {
            console.error('Username already taken');
            this.usernameExists = true;
          }
        }
      }
    });
  }

  private getSubmitData() {
    const formData = { ...this.registerForm.value };


    // Replace "Other" with custom salutation
    if (formData.salutation === 'Other') {
      if (!formData.customSalutation) {
        return;
      }
      formData.salutation = formData.customSalutation;
    }

    // Remove fields that shouldn't be sent to API
    delete formData.customSalutation;
    delete formData.passwordConfirmation;

    return formData;
  }

  get getFormControls() {
    return this.registerForm.controls;
  }
}
