import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormUtilService {

  static validateEmailCustom(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    const email = control.value.toLowerCase();

    // Check basic format
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!emailRegex.test(email)) {
      return { email: true };
    }

    // Additional checks
    const parts = email.split('@');
    if (parts.length !== 2) {
      return { email: true };
    }

    const [localPart, domain] = parts;

    // Check part before @
    if (localPart.length === 0 || localPart.length > 64) {
      return { email: true };
    }

    // Check domain has at least one dot
    if (!domain.includes('.')) {
      return { email: true };
    }

    // Check domain parts
    const domainParts = domain.split('.');
    const tld = domainParts[domainParts.length - 1];

    // TLD must be at least 2 characters
    if (tld.length < 2) {
      return { email: true };
    }

    return null;
  }
}
