import {Component, ViewEncapsulation, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth-service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {SelectModule} from 'primeng/select';
import {FormUtilService} from '../../../../shared/utils/form/form-util-service';
import {debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs';
import {environment} from '../../../../../environments/environment';
import {PictureUpload} from '../../../../core/picture-upload/picture-upload';
import {PasswordRequirement, PasswordValidationService} from '../../services/password-validation-service';
import {UserValidationService} from '../../../users/services/user-validation-service';
import {FloatLabel} from 'primeng/floatlabel';
import {InputGroup} from 'primeng/inputgroup';
import {InputGroupAddon} from 'primeng/inputgroupaddon';
import {IconField} from 'primeng/iconfield';
import {InputText} from 'primeng/inputtext';


@Component({
  selector: 'app-registration-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    SelectModule,
    PictureUpload,
    FloatLabel,
    InputGroup,
    InputGroupAddon,
    IconField,
    InputText,
  ],
  templateUrl: './registration-form.html',
  styleUrl: './registration-form.scss',
  encapsulation: ViewEncapsulation.None,
})
export class RegistrationForm {
  registerForm: FormGroup;
  salutations: string[];
  showCustomSalutation = signal<boolean>(false);
  usernameExists = signal<boolean>(false);
  emailExists = signal<boolean>(false);
  showPasswordRequirements = false;
  passwordRequirements: PasswordRequirement[];

  private formBuilder = inject(FormBuilder);
  private passwordValidator = inject(PasswordValidationService);
  public authService = inject(AuthService);
  private userValidationService = inject(UserValidationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;

  constructor() {
    this.salutations = environment.defaultSalutations;

    // Initialize password requirements from service
    this.passwordRequirements = this.passwordValidator.getPasswordRequirements();

    this.registerForm = this.formBuilder.group({
      salutation: ['', Validators.required],
      customSalutation: [''],
      firstName: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(environment.minIdentifierLength)]],
      email: ['', [Validators.required, FormUtilService.validateEmailCustom]],
      location: ['', [Validators.required]],
      password: ['', this.passwordValidator.getPasswordValidators()],
      passwordConfirmation: ['', [Validators.required]],
    }, {
      validators: this.passwordValidator.passwordMatchValidator()
    });

    this.registerForm.get('salutation')?.valueChanges.subscribe(value => {
      this.showCustomSalutation.set(value === this.salutations[this.salutations.length - 1]);
      const customSalutationControl = this.registerForm.get('customSalutation');

      if (this.showCustomSalutation()) {
        customSalutationControl?.setValidators([Validators.required]);
      } else {
        customSalutationControl?.clearValidators();
        customSalutationControl?.setValue('');
      }

      customSalutationControl?.updateValueAndValidity();
    });

    this.checkUsernameAvailability();
    this.checkEmailAvailability();

    // Password change handler for real-time feedback
    this.registerForm.get('password')?.valueChanges.subscribe(() => {
      this.passwordValidator.updatePasswordRequirements(
        this.registerForm.get('password'),
        this.passwordRequirements
      );
    });
  }

  private checkUsernameAvailability(): void {
    this.registerForm.get('username')?.valueChanges
      .pipe(
        filter(username => username.length >= environment.minIdentifierLength),
        debounceTime(environment.defaultDebounceTimeForFormFields),
        distinctUntilChanged(),
        switchMap(username => this.userValidationService.checkUsernameAvailability(username))
      )
      .subscribe(isAvailable => {
        this.usernameExists.set(!isAvailable);
      });
  }

  private checkEmailAvailability(): void {
    this.registerForm.get('email')?.valueChanges
      .pipe(
        filter(() => this.registerForm.get('email')?.valid === true),
        debounceTime(environment.defaultDebounceTimeForFormFields),
        distinctUntilChanged(),
        switchMap(email => this.userValidationService.checkEmailAvailability(email))
      )
      .subscribe(isAvailable => {
        this.emailExists.set(!isAvailable);
      });
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.registerForm.get(controlName);
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

  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    return this.passwordValidator.getPasswordStrength(password);
  }

  getPasswordStrengthLabel(): string {
    const strength = this.getPasswordStrength();
    return this.passwordValidator.getPasswordStrengthLabel(strength);
  }

  handleFileSelected(file: File) {
    this.selectedImage = file;
  }

  onSubmit() {
    if (this.registerForm.invalid || this.usernameExists() || this.emailExists()) {
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
        this.usernameExists.set(false);
        this.emailExists.set(false);
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigate([returnUrl]);
      },
      error: (err) => {
        console.error('Registration failed with status code:', err.status);
        if (err.status === 409) {
          const errorResponse = err.error;
          if (errorResponse.code === 'EMAIL_EXISTS') {
            this.emailExists.set(true);
          } else if (errorResponse.code === 'USERNAME_EXISTS') {
            this.usernameExists.set(true);
          }
        }
      }
    });
  }

  private getSubmitData() {
    const formData = {...this.registerForm.value};

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

  protected readonly environment = environment;
}
