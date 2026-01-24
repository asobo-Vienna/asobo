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


  public getAllByEventId(eventId: string): Observable<List<MediaItem>> {
    return this.http
      .get<MediaItem[]>(`${this.getMediaUrl(eventId)}`)
      .pipe(map(items => new List<MediaItem>(items)));
  }


  public upload(eventId: string, file: File): Observable<MediaItem> {
    const form = new FormData();
    form.append('mediumFile', file);
    return this.http.post<MediaItem>(`${this.getMediaUrl(eventId)}`, form);
  }


  public delete(eventId: string, item: MediaItem): Observable<MediaItem> {
    return this.http.delete<MediaItem>(`${this.getMediaUrl(eventId)}/${item.id}`);
  }


  private getMediaUrl(eventId: string): string {
    return `${environment.eventsEndpoint}/${eventId}/media`;
  }
}
