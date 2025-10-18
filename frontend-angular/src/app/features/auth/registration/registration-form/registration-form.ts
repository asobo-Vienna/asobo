import {Component, ViewEncapsulation, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import {AuthService} from '../../auth-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {SelectModule} from 'primeng/select';
import {FormUtilService} from '../../../../shared/utils/form/form-util-service';
import {debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs';
import {environment} from '../../../../../environments/environment';

@Component({
  selector: 'app-registration-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    SelectModule,
  ],
  templateUrl: './registration-form.html',
  styleUrl: './registration-form.scss',
  encapsulation: ViewEncapsulation.None,
})
export class RegistrationForm {
  registerForm: FormGroup;
  salutations: string[];
  showCustomSalutation: boolean;
  usernameExists: boolean;
  emailExists: boolean;
  showPasswordRequirements = false;

  passwordRequirements = [
    { key: 'minlength', label: 'At least 8 characters', met: false },
    { key: 'uppercase', label: 'At least one uppercase letter', met: false },
    { key: 'number', label: 'At least one number', met: false },
    { key: 'specialCharacter', label: 'At least one special character', met: false },
  ];

  passwordStrength: string = '';  // For PrimeNG strength indicator

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
      salutation: ['', Validators.required],
      customSalutation: [''],
      firstName: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, FormUtilService.strictEmailValidator]],
      location: ['', [Validators.required]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          this.hasUppercase.bind(this),
          this.hasNumber.bind(this),
          this.hasSpecialCharacter.bind(this),
        ],
      ],
      passwordConfirmation: ['', [Validators.required]],
    }, { validators: this.passwordMatchValidator.bind(this) });

    // Salutation change handler
    this.registerForm.get('salutation')?.valueChanges.subscribe(value => {
      this.showCustomSalutation = value === this.salutations[this.salutations.length - 1];
      const customSalutationControl = this.registerForm.get('customSalutation');

      if (this.showCustomSalutation) {
        customSalutationControl?.setValidators([Validators.required]);
      } else {
        customSalutationControl?.clearValidators();
        customSalutationControl?.setValue('');
      }

      customSalutationControl?.updateValueAndValidity();
    });

    // Username availability check
    this.registerForm.get('username')?.valueChanges
      .pipe(
        filter(username => username.length >= environment.minIdentifierLength),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(username => this.authService.checkUsernameAvailability(username))
      )
      .subscribe(isAvailable => {
        this.usernameExists = !isAvailable;
      });

    // Email availability check
    this.registerForm.get('email')?.valueChanges
      .pipe(
        filter(() => this.registerForm.get('email')?.valid === true),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(email => this.authService.checkEmailAvailability(email))
      )
      .subscribe(isAvailable => {
        this.emailExists = !isAvailable;
      });

    // Password change handler for real-time feedback
    this.registerForm
      .get('password')
      ?.valueChanges.subscribe(() => {
      this.updatePasswordRequirements();
    });
  }

  // Form-level validator: check if passwords match
  private passwordMatchValidator(form: AbstractControl): ValidationErrors | null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('passwordConfirmation')?.value;

    if (password && confirmPassword && password !== confirmPassword) {
      form.get('passwordConfirmation')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    const errors = form.get('passwordConfirmation')?.errors;
    if (errors) {
      delete errors['passwordMismatch'];
      if (Object.keys(errors).length === 0) {
        form.get('passwordConfirmation')?.setErrors(null);
      }
    }

    return null;
  }

  // Custom password validators
  private hasUppercase = (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value || !/[A-Z]/.test(value)) {
      return { uppercase: true };
    }
    return null;
  };

  private hasNumber = (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value || !/\d/.test(value)) {
      return { number: true };
    }
    return null;
  };

  private hasSpecialCharacter = (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value || !environment.specialCharactersForPW.test(value)) {
      return { specialCharacter: true };
    }
    return null;
  };

  // Update password requirements in real-time
  private updatePasswordRequirements(): void {
    const passwordControl = this.registerForm.get('password');
    if (!passwordControl)
      return;

    const value = passwordControl.value;
    const passwordErrors = passwordControl.errors || {};

    // Only show checkmarks if password has content
    if (!value) {
      this.passwordRequirements.forEach((requirement) => {
        requirement.met = false;
      });
      return;
    }

    this.passwordRequirements.forEach((requirement) => {
      requirement.met = !passwordErrors[requirement.key];
    });
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.registerForm.get(controlName);
    // double negation !! converts a value to a boolean
    return !!(control?.hasError(errorName) && control?.touched);
  }

  getErrorMessage(controlName: string): string | null {
    const control = this.registerForm.get(controlName);
    if (!control?.errors || !control?.touched)
      return null;

    const errors = control.errors;

    if (errors['required'])
      return 'This field is required';
    if (errors['minlength'])
      return `Minimum length is ${errors['minlength'].requiredLength}`;
    if (errors['uppercase'])
      return 'Must contain at least one uppercase letter';
    if (errors['number'])
      return 'Must contain at least one number';
    if (errors['specialCharacter'])
      return 'Must contain at least one special character';
    if (controlName === 'email' && errors['email'])
      return 'Please enter a valid email address';

    return null;
  }

  toggleRequirements(): void {
    this.showPasswordRequirements = !this.showPasswordRequirements;
  }

  // Calculate password strength
  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';

    let strength = 0;

    // Check each requirement
    if (password.length >= environment.minPWLength) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (environment.specialCharactersForPW.test(password)) strength++;

    if (strength <= 2) return 'weak';
    if (strength <= 3) return 'medium';
    return 'strong';
  }

  getPasswordStrengthLabel(): string {
    const strength = this.getPasswordStrength();
    switch (strength) {
      case 'weak':
        return 'Too simple';
      case 'medium':
        return 'Average complexity';
      case 'strong':
        return 'Complex password';
      default:
        return 'Choose a password';
    }
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
    if (!formDataObj) {
      return;
    }

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
        if (err.status === 409) {
          const errorResponse = err.error;
          if (errorResponse.code === 'EMAIL_EXISTS') {
            this.emailExists = true;
          } else if (errorResponse.code === 'USERNAME_EXISTS') {
            this.usernameExists = true;
          }
        }
      }
    });
  }

  private getSubmitData() {
    const formData = { ...this.registerForm.value };

    if (formData.salutation === 'Other') {
      if (!formData.customSalutation) {
        return;
      }
      formData.salutation = formData.customSalutation;
    }

    delete formData.customSalutation;
    delete formData.passwordConfirmation;

    return formData;
  }

  get getFormControls() {
    return this.registerForm.controls;
  }
}
