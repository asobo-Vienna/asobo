// password-validation.service.ts
import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { environment } from '../../../../environments/environment';

export interface PasswordRequirement {
  key: string;
  label: string;
  met: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PasswordValidationService {

  // Password requirements definition
  getPasswordRequirements(): PasswordRequirement[] {
    return [
      { key: 'minlength', label: `At least ${environment.minPWLength} characters`, met: false },
      { key: 'uppercase', label: 'At least one uppercase letter', met: false },
      { key: 'lowercase', label: 'At least one lowercase letter', met: false },
      { key: 'number', label: 'At least one number', met: false },
      { key: 'specialCharacter', label: 'At least one special character', met: false },
    ];
  }

  // Custom validators
  hasUppercase(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value || !/[A-Z]/.test(value)) {
        return { uppercase: true };
      }
      return null;
    };
  }

  hasLowercase(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value || !/[a-z]/.test(value)) {
        return { lowercase: true };
      }
      return null;
    };
  }

  hasNumber(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value || !/\d/.test(value)) {
        return { number: true };
      }
      return null;
    };
  }

  hasSpecialCharacter(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value || !environment.specialCharactersForPW.test(value)) {
        return { specialCharacter: true };
      }
      return null;
    };
  }

  // Password match validator (for form-level validation)
  passwordMatchValidator(): ValidatorFn {
    return (form: AbstractControl): ValidationErrors | null => {
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
    };
  }

  // Update requirements based on password control
  updatePasswordRequirements(
    passwordControl: AbstractControl | null,
    requirements: PasswordRequirement[]
  ): void {
    if (!passwordControl) return;

    const value = passwordControl.value;
    const passwordErrors = passwordControl.errors || {};

    // Only show checkmarks if password has content
    if (!value) {
      requirements.forEach((requirement) => {
        requirement.met = false;
      });
      return;
    }

    requirements.forEach((requirement) => {
      requirement.met = !passwordErrors[requirement.key];
    });
  }

  // Calculate password strength
  getPasswordStrength(password: string): string {
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

  // Get strength label
  getPasswordStrengthLabel(strength: string): string {
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

  // Get all password validators
  getPasswordValidators(): ValidatorFn[] {
    return [
      Validators.required,
      Validators.minLength(environment.minPWLength),
      this.hasUppercase(),
      this.hasLowercase(),
      this.hasNumber(),
      this.hasSpecialCharacter(),
    ];
  }
}
