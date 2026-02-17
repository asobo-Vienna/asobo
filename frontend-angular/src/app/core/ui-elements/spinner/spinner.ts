import { Component, input } from '@angular/core';

@Component({
  selector: 'app-spinner',
  imports: [],
  templateUrl: './spinner.html',
  styleUrl: './spinner.scss'
})
export class Spinner {
  type = input<'inline' | 'overlay'>('inline');
  message = input<string>();
  size = input<'sm' | 'md' | 'lg'>('md');
}
