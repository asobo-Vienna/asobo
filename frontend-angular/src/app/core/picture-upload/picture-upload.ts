import {Component, ElementRef, input, OnInit, output, signal, viewChild} from '@angular/core';
import {SecureImagePipe} from '../pipes/secure-image-pipe';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-picture-upload',
  templateUrl: './picture-upload.html',
  imports: [
    SecureImagePipe,
    AsyncPipe,
    //NgOptimizedImage
  ],
  styleUrl: './picture-upload.scss'
})
export class PictureUpload implements OnInit {
  pictureBox = viewChild<ElementRef<HTMLElement>>('pictureBox');
  currentImage = input<string | null>(null);
  showPlusBeforeUpload = input<boolean>(false);
  shape = input<string>('circle');
  caption = input<string>('Add profile picture');
  disabled = input<boolean>(false);
  fileSelected = output<File>();
  preview = signal<string | null>(null);
  dimensions = input<[number, number]>([20, 20]);

  ngOnInit(): void {
    this.setFrameDimensions(this.dimensions()[0], this.dimensions()[1]);
    if (!this.showPlusBeforeUpload()) {
      this.removePictureBoxBorder();
    }
  }

  async onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Please select an image.');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.preview.set(e.target.result);
      this.fileSelected.emit(file);

      this.removePictureBoxBorder();
    };
    reader.readAsDataURL(file);
  }

  private removePictureBoxBorder() : void {
    const box = this.pictureBox()?.nativeElement;
    if (box) {
      box.style.border = 'none';
      box.style.backgroundColor = 'transparent';
    }
  }

  private setFrameDimensions(width: number, height: number) : void {
    const box = this.pictureBox()?.nativeElement;
    if (box) {
      if (width === height) {
        // Square: use aspect-ratio
        box.style.aspectRatio = '1/1';
        box.style.width = '100%';
        box.style.maxWidth = String(width) + 'vw';
        box.style.height = 'auto';
      } else {
        // Rectangle: set both dimensions explicitly
        box.style.aspectRatio = '1.5/1';
        box.style.width = '100%';
        box.style.maxWidth = String(width) + 'vw';
        box.style.height = String(height) + 'vw';
        box.style.maxHeight = String(height) + 'vw';
        box.style.minHeight = '133.33px';
      }
    }
  }

  setShapeCssClass() : string {
    const cssClass: string = 'picture-box';
    if (this.shape() === 'rectangle') {
      return cssClass + ' rectangle-picture-box';
    }
    return cssClass;
  }
}
