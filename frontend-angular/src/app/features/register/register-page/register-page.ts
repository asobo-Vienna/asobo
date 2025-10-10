import { Component } from '@angular/core';
import {Password} from "primeng/password";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-register-page',
    imports: [
        Password,
        ReactiveFormsModule,
        RouterLink,
    ],
  templateUrl: './register-page.html',
  styleUrl: './register-page.scss'
})
export class RegisterPage {

}
