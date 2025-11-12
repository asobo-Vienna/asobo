import {Component, ViewEncapsulation, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth-service';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {CheckboxModule} from 'primeng/checkbox';
import {IconField} from 'primeng/iconfield';
import {InputGroup} from 'primeng/inputgroup';
import {InputGroupAddon} from 'primeng/inputgroupaddon';
import {InputText} from 'primeng/inputtext';

@Component({
  selector: 'app-login-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    CheckboxModule,
    IconField,
    InputGroup,
    InputGroupAddon,
    InputText,
  ],
  templateUrl: './login-form.html',
  styleUrl: './login-form.scss',
  encapsulation: ViewEncapsulation.None
})
export class LoginForm {
  loginForm: FormGroup;
  loginFailed: boolean;

  hasPasswordValue = signal(false);

  private formBuilder = inject(FormBuilder);
  public authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  constructor() {
    this.loginFailed = false;
    this.loginForm = this.formBuilder.group({
      identifier: ['', [
        Validators.required,
      ]],
      password: ['', [
        Validators.required,
      ]],
      rememberMe: [false]
    });

    this.loginForm.get('password')?.valueChanges.subscribe(value => {
      this.hasPasswordValue.set(value?.length > 0);
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Login successful:', response);
        this.loginFailed = false;
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigate([returnUrl]); // TODO: decide where to navigate to after login
      },
      error: (err) => {
        console.log('Login failed with status code', err.status);
        // TODO: Show error message to user
        if (err.status === 401) {
          console.log("401 unauthorized");
          this.loginFailed = true;
        }
      }
    });
  }

  get getFormControls() {
    return this.loginForm.controls;
  }
}
