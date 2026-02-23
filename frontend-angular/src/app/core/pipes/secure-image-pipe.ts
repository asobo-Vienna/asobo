import { Pipe, PipeTransform, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { BehaviorSubject, Observable } from 'rxjs';

@Pipe({
  name: 'secureImage',
  standalone: true,
})
export class SecureImagePipe implements PipeTransform, OnDestroy {
  private currentUrl: string | null = null;

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {}

  transform(url: string | null): Observable<SafeUrl> {
    const subject = new BehaviorSubject<SafeUrl>('');

    if (!url) {
      return subject.asObservable();
    }

    // Revoke previous blob URL to avoid memory leaks
    if (this.currentUrl) {
      URL.revokeObjectURL(this.currentUrl);
    }

    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.currentUrl = objectUrl;
        subject.next(this.sanitizer.bypassSecurityTrustUrl(objectUrl));
      },
      error: () => {
        subject.next('');
      },
    });

    return subject.asObservable();
  }

  ngOnDestroy(): void {
    if (this.currentUrl) {
      URL.revokeObjectURL(this.currentUrl);
    }
  }
}
