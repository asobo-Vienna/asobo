import { Component, computed, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { AuthService } from '../../auth/services/auth-service';
import { UserProfileService } from './user-profile-service';
import { ProfilePictureUpload } from '../profile-picture-upload/profile-picture-upload';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { InputText } from 'primeng/inputtext';
import { FloatLabel } from 'primeng/floatlabel';
import { InputGroup } from 'primeng/inputgroup';
import { InputGroupAddon } from 'primeng/inputgroupaddon';
import { Password } from 'primeng/password';
import {environment} from '../../../../environments/environment';
import {FormUtilService} from '../../../shared/utils/form/form-util-service';
import {PasswordRequirement, PasswordValidationService} from '../../auth/services/password-validation-service';

@Component({
  selector: 'app-user-profile',
  imports: [
    ProfilePictureUpload,
    IconField,
    InputIcon,
    InputText,
    FormsModule,
    FloatLabel,
    InputGroup,
    InputGroupAddon,
    Password,
    ReactiveFormsModule,
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss',
})
export class UserProfile implements OnInit {
  private userProfileService = inject(UserProfileService);
  public authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private formBuilder = inject(FormBuilder);
  private passwordValidator = inject(PasswordValidationService);

  showPasswordRequirements = false;
  passwordRequirements: PasswordRequirement[];

  updateForm: FormGroup;

  // Profile data from service
  userProfile = this.userProfileService.userProfile;

  // Image handling
  previewUrl = signal<string | ArrayBuffer | null>(null);
  selectedImage: File | null = null;
  selectedImageUrl = computed(() => this.userProfile().pictureUrl);
  displayImage = computed(() => this.previewUrl() || this.selectedImageUrl());

  // Editing state
  private editingFields = signal(new Set<string>());

  username = signal('');

  // Password fields
  password = signal('');
  passwordConfirmation = signal('');

  constructor() {
    // Initialize password requirements from service
    this.passwordRequirements = this.passwordValidator.getPasswordRequirements();

    this.updateForm = this.formBuilder.group({
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
      //validators: this.passwordValidator.passwordMatchValidator()
    });
  }

  ngOnInit() {
    // Disable all form controls initially
    this.updateForm.disable();

    // Get username from route params and load profile
    this.route.params.subscribe(params => {
      const username = params['username'];
      if (username) {
        this.loadUserProfile(username);
      } else {
        // Fallback: if no username in route, redirect to logged-in user's profile
        const currentUsername = this.authService.currentUser()?.username;
        if (currentUsername) {
          this.router.navigate(['/profile', currentUsername]);
        }
      }
    });
  }

  loadUserProfile(username: string) {
    this.userProfileService.getUserByUsername(username).subscribe({
      next: (user) => {
        this.username.set(user.username);

        this.updateForm.patchValue({
          username: user.username,
          firstName: user.firstName,
          surname: user.surname,
          location: user.location || '',
          email: user.email,
          salutation: user.salutation || '',
          //customSalutation: user.customSalutation || ''
        });

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
        // Optionally redirect to 404 or home
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
  isEditingUsername() { return this.editingFields().has('username'); }
  isEditingFirstName() { return this.editingFields().has('firstName'); }
  isEditingSurname() { return this.editingFields().has('surname'); }
  isEditingLocation() { return this.editingFields().has('location'); }
  isEditingEmail() { return this.editingFields().has('email'); }
  isEditingPassword() { return this.editingFields().has('password'); }

  toggleEdit(field: 'username' | 'firstName' | 'surname' | 'location' | 'email' | 'password') {
    if (!this.isOwnProfile()) {
      console.error('Cannot edit another user\'s profile');
      return;
    }

    const control =  this.updateForm.get(field);
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

    this.userProfileService.updateField(fieldName, value).subscribe({
      next: () => {
        console.log(`${fieldName} updated successfully`);
        const fields = this.editingFields();
        fields.delete(fieldName);
        this.editingFields.set(new Set(fields));
      },
      error: (err) => {
        console.error(`Failed to update ${fieldName}:`, err);
        this.loadUserProfile(this.username());
      }
    });
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

  // Password handling
  onPasswordFocus() {
    // Enable editing when user focuses on password field
    if (!this.isEditingPassword()) {
      const fields = this.editingFields();
      fields.add('password');
      this.editingFields.set(new Set(fields));
    }
    // Show password requirements
    this.showPasswordRequirements = true;
  }

  onPasswordBlur() {
    // Hide password requirements
    this.showPasswordRequirements = false;
    // Keep editing enabled - don't disable until cancel/save
  }

  toggleRequirements(): void {
    this.showPasswordRequirements = !this.showPasswordRequirements;
  }

  updatePassword() {
    if (!this.isOwnProfile()) {
      console.error('Cannot change another user\'s password');
      return;
    }

    const pwd = this.password();
    const confirm = this.passwordConfirmation();

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
    this.password.set('');
    this.passwordConfirmation.set('');
    this.showPasswordRequirements = false;
    const fields = this.editingFields();
    fields.delete('password');
    this.editingFields.set(new Set(fields));
  }

  get getFormControls() {
    return this.updateForm.controls;
  }

  protected readonly environment = environment;
}
