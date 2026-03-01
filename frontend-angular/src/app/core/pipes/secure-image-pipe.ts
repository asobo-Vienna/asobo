import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

@Pipe({
  name: 'secureImage',
  standalone: true,
})
export class SecureImagePipe implements PipeTransform {
  private cache = new Map<string, Observable<SafeUrl>>();

  constructor(private http: HttpClient, private sanitizer: DomSanitizer) {}

  transform(url: string | null): Observable<SafeUrl> {
    if (!url) return of('');

    if (this.cache.has(url)) {
      return this.cache.get(url)!;
    }

    const obs$ = this.http.get(url, { responseType: 'blob' }).pipe(
      map(blob => this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))),
      shareReplay(1)
    );

    this.cache.set(url, obs$);
    return obs$;
  }
}
