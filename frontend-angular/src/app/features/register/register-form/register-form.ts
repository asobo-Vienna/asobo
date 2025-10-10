import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../auth/auth-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Password} from 'primeng/password';

@Component({
  selector: 'app-register-form',
  imports: [
    Password,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register-form.html',
  styleUrl: './register-form.scss'
})
export class RegisterForm {
  registerForm: FormGroup;

  private formBuilder = inject(FormBuilder);
  public authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  constructor() {
    this.registerForm = this.formBuilder.group({
      identifier: ['', [
        Validators.required,
      ]],
      password: ['', [
        Validators.required,
      ]]
    });
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
  }

  get getFormControls() {
    return this.registerForm.controls;
  }
}
