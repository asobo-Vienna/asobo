import {Component, computed, inject, signal} from '@angular/core';
import {AuthService} from '../../auth/auth-service';
import {Router} from '@angular/router';
import {UserProfileService} from './user-profile-service';
import {ProfilePictureUpload} from '../profile-picture-upload/profile-picture-upload';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';

@Component({
  selector: 'app-user-profile',
  imports: [
    ProfilePictureUpload,
    IconField,
    InputIcon,
    InputText,
    FormsModule,
    FloatLabel
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss',
})
export class UserProfile {
  private userProfileService = inject(UserProfileService);
  authService = inject(AuthService);
  private router = inject(Router);

  previewUrl = signal<string | ArrayBuffer | null>(null);
  userProfile = this.userProfileService.userProfile;
  selectedImage: File | null = null;
  selectedImageUrl = computed(() => this.userProfile().pictureUrl);
  displayImage = computed(() => this.previewUrl() || this.selectedImageUrl());

  isEditingUsername = signal(false);
  isEditingFirstName = signal(false);
  isEditingSurname = signal(false);
  isEditingLocation = signal(false);
  username = signal('')
  firstName = signal('');
  surname = signal('');
  location = signal('');

  toggleEdit(field: 'username' | 'firstName' | 'surname' | 'location') {
    if (field === 'username') {
      this.isEditingUsername.set(!this.isEditingUsername)
    } else if (field === 'firstName') {
      this.isEditingFirstName.set(!this.isEditingFirstName());
    } else if (field === 'surname') {
      this.isEditingSurname.set(!this.isEditingSurname());
    } else if (field === 'location') {
      this.isEditingLocation.set(!this.isEditingLocation());
    }
  }

  handleFileSelected(file: File) {
    this.selectedImage = file;
  }
}
