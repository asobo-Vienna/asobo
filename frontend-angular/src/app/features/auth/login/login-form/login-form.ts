import {Component, ViewEncapsulation, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../auth-service';
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {CheckboxModule} from 'primeng/checkbox';

@Component({
  selector: 'app-login-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    CheckboxModule,
  ],
  templateUrl: './login-form.html',
  styleUrl: './login-form.scss',
  encapsulation: ViewEncapsulation.None
})
export class LoginForm {
  loginForm: FormGroup;
  loginFailed: boolean;

  private formBuilder = inject(FormBuilder);
  public authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  constructor(
  ) {
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
