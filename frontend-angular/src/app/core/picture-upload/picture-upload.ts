import {Component, ElementRef, input, output, signal, viewChild} from '@angular/core';

@Component({
  selector: 'app-picture-upload',
  templateUrl: './picture-upload.html',
  imports: [
    //NgOptimizedImage
  ],
  styleUrl: './picture-upload.scss'
})
export class PictureUpload {
  pictureBox = viewChild<ElementRef<HTMLElement>>('pictureBox');
  currentImage = input<string | ArrayBuffer | null>(null);
  showPlusBeforeUpload = input<boolean>(false);
  shape = input<string>('circle');
  caption = input<string>('Add profile picture');
  disabled = input<boolean>(false);
  fileSelected = output<File>();
  preview = signal<string | null>(null);
  dimensions = input<[number, number]>([20, 20]);

  ngOnInit() {
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
        box.style.aspectRatio = '1/1';
      } else {
        box.style.aspectRatio = '1.5/1';
        box.style.minHeight = '133.33px';
      }
      box.style.width = String(width) + 'vw';
      box.style.height = String(height) + 'vw';
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
