import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {MediaItem} from '../models/media-item';
import {map, Observable} from 'rxjs';
import {List} from '../../../core/data_structures/lists/list';

@Injectable({
  providedIn: 'root'
})
export class MediaService {
  private http = inject(HttpClient);


  getAllByEventId(eventId: string): Observable<List<MediaItem>> {
    return this.http
      .get<MediaItem[]>(`${this.getMediaUrl(eventId)}`)
      .pipe(map(items => new List<MediaItem>(items)));
  }


  upload(eventId: string, file: File): Observable<MediaItem> {
    const form = new FormData();
    form.append('mediumFile', file);
    return this.http.post<MediaItem>(`${this.getMediaUrl(eventId)}`, form);
  }


  delete(eventId: string, item: MediaItem): Observable<MediaItem> {
    return this.http.delete<MediaItem>(`${environment.eventsAddress}/${eventId}/media/${item.id}`);
  }


  private getMediaUrl(eventId: string): string {
    return `${environment.eventsAddress}/${eventId}/media`;
  }
}
