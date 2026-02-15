import {Component, computed, inject, signal, OnInit, output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../auth/services/auth-service';
import {UserProfileService} from '../../services/user-profile-service';
import {PictureUpload} from '../../../../core/picture-upload/picture-upload';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {FloatLabel} from 'primeng/floatlabel';
import {InputGroup} from 'primeng/inputgroup';
import {InputGroupAddon} from 'primeng/inputgroupaddon';
import {Password} from 'primeng/password';
import {environment} from '../../../../../environments/environment';
import {FormUtilService} from '../../../../shared/utils/form/form-util-service';
import {PasswordRequirement, PasswordValidationService} from '../../../auth/services/password-validation-service';
import {debounceTime, distinctUntilChanged, filter, map, switchMap} from 'rxjs';
import {UserValidationService} from '../../services/user-validation-service';
import {NgClass} from '@angular/common';
import {Select} from 'primeng/select';
import {Textarea} from 'primeng/textarea';
import {List} from '../../../../core/data_structures/lists/list';
import { Event } from '../../../events/models/event';
import {UserService} from '../../services/user-service';

@Component({
  selector: 'app-user-profile-form',
  imports: [
    PictureUpload,
    IconField,
    InputIcon,
    InputText,
    FormsModule,
    FloatLabel,
    InputGroup,
    InputGroupAddon,
    Password,
    ReactiveFormsModule,
    NgClass,
    Select,
    Textarea,
  ],
  templateUrl: './user-profile-form.html',
  styleUrl: './user-profile-form.scss',
})
export class UserProfileForm implements OnInit {
  private userProfileService = inject(UserProfileService);
  private userService = inject(UserService);
  public authService = inject(AuthService);
  private userValidationService = inject(UserValidationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private formBuilder = inject(FormBuilder);
  private passwordValidator = inject(PasswordValidationService);

  showPasswordRequirements = false;
  passwordRequirements: PasswordRequirement[];

  updateForm: FormGroup;
  salutations: string[];
  countryCodes: {label: string, value: string}[] = [];
  showCustomSalutation = signal(false);
  usernameExists = signal(false);
  emailExists = signal(false);

  userProfile = this.userProfileService.userProfile;

  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;
  selectedImageUrl = computed(() => this.userProfile().pictureUrl);
  displayImage = computed(() => this.previewUrl() || this.selectedImageUrl());

  // Editing state
  private editingFields = signal(new Set<string>());

  username = signal<string>('');
  userId = output<string>();
  events = signal<List<Event>>(new List<Event>());

  constructor() {
    this.salutations = environment.defaultSalutations;

    // Initialize password requirements from service
    this.passwordRequirements = this.passwordValidator.getPasswordRequirements();

    this.updateForm = this.formBuilder.group({
      aboutMe: ['', [Validators.maxLength(environment.maxAboutMeLength)]],
      salutation: ['', Validators.required],
      customSalutation: [''],
      firstName: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(environment.minIdentifierLength)]],
      email: ['', [Validators.required, FormUtilService.validateEmailCustom]],
      location: ['', [Validators.required]],
      country: ['', [Validators.required]],
      password: [''], // make pw optional
      passwordConfirmation: [''],
    }, {
      validators: this.passwordValidator.passwordMatchValidator()
    });

    this.updateForm.get('salutation')?.valueChanges.subscribe(value => {
      // Only show custom salutation field if viewing own profile and "Other" is selected
      if (this.isOwnProfile()) {
        this.showCustomSalutation.set(value === this.salutations[this.salutations.length - 1]);
        const customSalutationControl = this.updateForm.get('customSalutation');

        if (this.showCustomSalutation()) {
          customSalutationControl?.setValidators([Validators.required]);
        } else {
          customSalutationControl?.clearValidators();
          customSalutationControl?.setValue('');
        }

        customSalutationControl?.updateValueAndValidity();
      } else {
        // Always hide custom field when viewing another user's profile
        this.showCustomSalutation.set(false);
      }
    });

    this.checkUsernameAvailability();
    this.checkEmailAvailability();

    this.updateForm.get('password')?.valueChanges.subscribe(() => {
      this.passwordValidator.updatePasswordRequirements(
        this.updateForm.get('password'),
        this.passwordRequirements
      );
    });
  }

  ngOnInit(): void {
    // Disable all form controls initially
    this.updateForm.disable();
    this.enableField('password');
    this.enableField('passwordConfirmation');
    this.enableField('salutation');
    this.enableField('country');
    //this.enableField('customSalutation');

    this.loadCountries();

    // Get username from route params and load profile
    this.route.params.subscribe(params => {
      const username = params['username'];
      if (username) {
        this.loadUserProfile(username);
      } else {
        // Fallback: if no username in route, redirect to logged-in user's profile
        const currentUsername = this.authService.currentUser()?.username;
        if (currentUsername) {
          this.router.navigate(['/user', currentUsername]);
        }
      }
    });
  }

  private loadCountries(): void {
    this.userService.getCountryCodes().subscribe({
      next: (codes) => {
        this.countryCodes = codes.map(code => ({
          label: new Intl.DisplayNames(['en'], {type: 'region'}).of(code) || code,
          value: code
        }));
      },
      error: (err) => console.error('Error loading countries:', err)
    });
  }

  private enableField(fieldName: string) {
    const fields = this.editingFields();
    fields.add(fieldName);
    this.editingFields.set(new Set(fields));
    this.updateForm.get(fieldName)?.enable({ emitEvent: true });
  }

  private checkUsernameAvailability(): void {
    this.updateForm.get('username')?.valueChanges
      .pipe(
        filter(username => {
          // Don't check if not viewing own profile
          if (!this.isOwnProfile()) {
            this.usernameExists.set(false);
            return false;
          }

          // Don't check if not editing
          if (!this.isEditingUsername()) {
            this.usernameExists.set(false);
            return false;
          }

          // Don't check if same as current username
          if (username === this.authService.currentUser()?.username) {
            this.usernameExists.set(false);
            return false;
          }

          // Reset if too short
          if (!username || username.length < environment.minIdentifierLength) {
            this.usernameExists.set(false);
            return false;
          }

          return true;
        }),
        debounceTime(environment.defaultDebounceTimeForFormFields),
        distinctUntilChanged(),
        switchMap(username => this.userValidationService.checkUsernameAvailability(username))
      )
      .subscribe(response => {
        this.usernameExists.set(!response.available);
      });
  }

  private checkEmailAvailability(): void {
    this.updateForm.get('email')?.valueChanges
      .pipe(
        filter(email => {
          // Don't check if not viewing own profile
          if (!this.isOwnProfile()) {
            this.emailExists.set(false);
            return false;
          }

          // Don't check if not editing
          if (!this.isEditingEmail()) {
            this.emailExists.set(false);
            return false;
          }

          // Don't check if same as current email
          if (email === this.authService.currentUser()?.email) {
            this.emailExists.set(false);
            return false;
          }

          // Reset if invalid
          if (!this.updateForm.get('email')?.valid) {
            this.emailExists.set(false);
            return false;
          }

          return true;
        }),
        debounceTime(environment.defaultDebounceTimeForFormFields),
        distinctUntilChanged(),
        switchMap(email => this.userValidationService.checkEmailAvailability(email))
      )
      .subscribe(response => {
        this.emailExists.set(!response.available);
      });
  }

  loadUserProfile(username: string) {
    this.userProfileService.getUserByUsername(username).subscribe({
      next: (user) => {
        this.username.set(user.username);
        this.userId.emit(user.id);

        // Check if salutation matches predefined options
        const isCustomSalutation = user.salutation && !this.salutations.includes(user.salutation);

        // Determine if viewing another user's profile
        const viewingOtherProfile = !this.isOwnProfile();

        if (isCustomSalutation && viewingOtherProfile) {
          // When viewing another user's custom salutation, add it to the options
          this.salutations = [...environment.defaultSalutations.filter(salutation => salutation !== 'Other'), user.salutation];

          this.updateForm.patchValue({
            aboutMe: user.aboutMe,
            username: user.username,
            firstName: user.firstName,
            surname: user.surname,
            location: user.location || '',
            country: user.country,
            email: user.email,
            salutation: user.salutation,
            customSalutation: '',
          });

          this.showCustomSalutation.set(false);
        } else if (isCustomSalutation && !viewingOtherProfile) {
          // Viewing own profile with custom salutation - show "Other" + custom field
          this.salutations = environment.defaultSalutations;

          this.updateForm.patchValue({
            aboutMe: user.aboutMe,
            username: user.username,
            firstName: user.firstName,
            surname: user.surname,
            location: user.location || '',
            country: user.country,
            email: user.email,
            salutation: 'Other',
            customSalutation: user.salutation,
          });

          this.showCustomSalutation.set(true);
        } else {
          this.salutations = environment.defaultSalutations;

          this.updateForm.patchValue({
            aboutMe: user.aboutMe,
            username: user.username,
            firstName: user.firstName,
            surname: user.surname,
            location: user.location || '',
            country: user.country,
            email: user.email,
            salutation: user.salutation || '',
            customSalutation: '',
          });

          this.showCustomSalutation.set(false);
        }

        // Disable all fields initially if viewing own profile
        // or disable all if viewing someone else's profile
        if (this.isOwnProfile()) {
          // Disable all except those being edited
          Object.keys(this.updateForm.controls).forEach(key => {
            if (!this.editingFields().has(key)) {
              this.updateForm.get(key)?.disable();
            }
          });
        } else {
          // Disable all fields when viewing someone else's profile
          this.updateForm.disable();
        }
      },
      error: (err) => {
        console.error('Failed to load user profile:', err);
      }
    });
  }

  // Check if viewing own profile
  isOwnProfile(): boolean {
    const loggedInUser = this.authService.currentUser();
    const viewedUser = this.userProfile();
    return loggedInUser?.username === viewedUser.username;
  }

  // Editing state helpers
  isEditingAboutMe(): boolean {
    return this.editingFields().has('aboutMe');
  }

  isEditingCustomSalutation(): boolean {
    return this.editingFields().has('customSalutation');
  }

  isEditingUsername(): boolean {
    return this.editingFields().has('username');
  }

  isEditingFirstName(): boolean {
    return this.editingFields().has('firstName');
  }

  isEditingSurname(): boolean {
    return this.editingFields().has('surname');
  }

  isEditingLocation(): boolean {
    return this.editingFields().has('location');
  }

  isEditingEmail(): boolean {
    return this.editingFields().has('email');
  }

  isEditingPassword(): boolean {
    return this.editingFields().has('password');
  }

  toggleEdit(field: 'customSalutation' | 'aboutMe' | 'username' | 'firstName' | 'surname' | 'location' | 'email' | 'password'): void {
    if (!this.isOwnProfile()) {
      console.error('Cannot edit another user\'s profile');
      return;
    }

    const control = this.updateForm.get(field);
    const fields = this.editingFields();

    if (fields.has(field)) {
      fields.delete(field);
      control?.disable();
      // Cancel - reload original value
      this.loadUserProfile(this.username());
    } else {
      fields.add(field);
      control?.enable();
    }
    this.editingFields.set(new Set(fields));
  }

  // Save field on blur
  saveField(fieldName: string) {
    if (!this.isOwnProfile()) {
      console.error('Cannot edit another user\'s profile');
      return;
    }

    if (!this.editingFields().has(fieldName)) {
      return;
    }

    const control = this.updateForm.get(fieldName);
    const value = control?.value;

    if (!value || value.trim() === '') {
      console.error(`${fieldName} cannot be empty`);
      return;
    }

    // Check if field is valid
    if (control?.invalid) {
      console.error(`${fieldName} is invalid`);
      return;
    }

    if (fieldName === 'aboutMe' && value === this.authService.currentUser()?.aboutMe) {
      console.warn(`${fieldName} coincides with logged in user's about me`);
      return;
    }

    if (fieldName === 'username' && value === this.authService.currentUser()?.username) {
      console.warn(`${fieldName} coincides with logged in user's username`);
      return;
    }

    if (fieldName === 'email' && value === this.authService.currentUser()?.email) {
      console.warn(`${fieldName} coincides with logged in user's email address`);
      return;
    }

    if (fieldName in ['salutation', 'customSalutation'] && value === this.authService.currentUser()?.salutation) {
      console.warn(`${fieldName} coincides with logged in user's salutation`);
      return;
    }

    if (fieldName === 'country' && value === this.authService.currentUser()?.country) {
      console.warn(`${fieldName} coincides with logged in user's country`);
      return;
    }

    // if the field to update is customSalutation, actually set the salutation to the value of customSalutation
    fieldName = fieldName === 'customSalutation' ? 'salutation' : fieldName;

    this.userProfileService.updateField(fieldName, value).subscribe({
      next: (response) => {
        console.log(`${fieldName} updated successfully`);

        // Update the logged-in user's data in AuthService
        this.authService.currentUser.set(response.user);

        this.updateForm.patchValue({ [fieldName]: value });
        const fields = this.editingFields();
        fields.delete(fieldName);
        this.editingFields.set(new Set(fields));

        // Navigate to new username URL
        if (fieldName === 'username') {
          this.router.navigate(['/user', response.user.username], { replaceUrl: true });
          return;
        }

        this.loadUserProfile(response.user.username);

        // Re-enable salutation and country fields after reload
        if (fieldName === 'salutation') {
          this.enableField('salutation');
        }

        if (fieldName === 'country') {
          this.enableField('country');
        }
      },
      error: (err) => {
        console.error(`Failed to update ${fieldName}:`, err);
        this.loadUserProfile(this.username());
      }
    });
  }

  onSalutationChange(event: any) {
    const isOther = event.value === 'Other';
    this.showCustomSalutation.set(isOther);

    if (!isOther) {
      this.saveField('salutation');
    }
  }

  onCountryChange(event: any) {
      this.saveField('country');
  }

  handleFileSelected(file: File) {
    if (!this.isOwnProfile()) {
      console.error('Cannot edit another user\'s profile picture');
      return;
    }

    this.selectedImage = file;

    // Create preview
    const reader = new FileReader();
    reader.onload = (e) => {
      this.previewUrl.set(e.target?.result || null);
    };
    reader.readAsDataURL(file);

    // Upload immediately
    this.uploadProfilePicture();
  }

  uploadProfilePicture() {
    if (!this.selectedImage) return;

    const formData = new FormData();
    formData.append('profilePicture', this.selectedImage);

    this.userProfileService.updateProfilePicture(formData).subscribe({
      next: () => {
        console.log('Profile picture updated');
        this.loadUserProfile(this.username());
        this.previewUrl.set(null);
        this.selectedImage = null;
      },
      error: (err) => {
        console.error('Failed to update profile picture:', err);
        this.previewUrl.set(null);
      }
    });
  }

  onPasswordFocus(): void {
    const passwordControl = this.updateForm.get('password');
    const confirmControl = this.updateForm.get('passwordConfirmation');

    // Add validators when user focuses on password
    passwordControl?.setValidators(this.passwordValidator.getPasswordValidators());
    confirmControl?.setValidators([Validators.required]);

    passwordControl?.updateValueAndValidity();
    confirmControl?.updateValueAndValidity();

    if (!this.isEditingPassword()) {
      const fields = this.editingFields();
      fields.add('password');
      this.editingFields.set(new Set(fields));
    }

    this.showPasswordRequirements = true;
  }

  onPasswordBlur(): void {
    // Hide password requirements
    this.showPasswordRequirements = false;
    // Keep editing enabled - don't disable until cancel/save
  }

  getPasswordStrength(): string {
    const password = this.updateForm.get('password')?.value;
    return this.passwordValidator.getPasswordStrength(password);
  }

  getPasswordStrengthLabel(): string {
    const strength = this.getPasswordStrength();
    return this.passwordValidator.getPasswordStrengthLabel(strength);
  }

  updatePassword() {
    if (!this.isOwnProfile()) {
      console.error('Cannot change another user\'s password');
      return;
    }

    const pwd = this.updateForm.get('password')?.value;
    const confirm = this.updateForm.get('passwordConfirmation')?.value;

    if (!pwd || pwd.length < environment.minPWLength) {
      console.error(`Password must be at least ${environment.minPWLength} characters`);
      return;
    }

    if (pwd !== confirm) {
      console.error('Passwords do not match');
      return;
    }

    this.userProfileService.updatePassword(pwd).subscribe({
      next: () => {
        console.log('Password updated successfully');
        this.cancelPasswordEdit();
      },
      error: (err) => {
        console.error('Failed to update password:', err);
      }
    });
  }

  cancelPasswordEdit() {
    const passwordControl = this.updateForm.get('password');
    const confirmControl = this.updateForm.get('passwordConfirmation');

    // Reset controls (clears value and validation state)
    passwordControl?.reset();
    confirmControl?.reset();

    // Remove validators since password is optional
    passwordControl?.clearValidators();
    confirmControl?.clearValidators();

    passwordControl?.updateValueAndValidity();
    confirmControl?.updateValueAndValidity();

    this.showPasswordRequirements = false;

    const fields = this.editingFields();
    fields.delete('password');
    this.editingFields.set(new Set(fields));
  }

  showPasswordConfirmation() {
    return this.updateForm.get('password')?.value && this.updateForm.get('password')?.valid && this.isEditingPassword();
  }

  disablePWUpdateButton() {
    const pwFieldValue = this.updateForm.get('password')?.value;
    return !pwFieldValue ||
      this.getFormControls['password'].invalid ||
      this.getFormControls['passwordConfirmation'].invalid ||
      pwFieldValue !== this.updateForm.get('passwordConfirmation')?.value;
  }

  get getFormControls() {
    return this.updateForm.controls;
  }

  protected readonly environment = environment;
}
