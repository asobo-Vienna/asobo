import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {
  AutocompleteItem,
  EventSearchResult,
  GlobalSearchResponse,
  UserSearchResult
} from '../../../shared/entities/search';
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private http = inject(HttpClient);

  search(query: string): Observable<AutocompleteItem[]> {
    return this.http
      .get<GlobalSearchResponse>(`${environment.apiBaseUrl}/search?q=${encodeURIComponent(query)}`)
      .pipe(
        map((response) => [
          ...response.events.map((e: EventSearchResult) => ({
            ...e,
            name: e.title ?? 'Untitled event',
            pictureURI: UrlUtilService.getMediaUrl(
              e.pictureURI || '/uploads/event-cover-pictures/event-cover-default.svg'
            ),
            additionalInfo: e.date
              ? new Date(e.date).toLocaleDateString()
              : '',
            location: e.location ?? 'Unknown location',
            type: 'EVENT' as const,
          })),
          ...response.users.map((u: UserSearchResult) => ({
            ...u,
            name: u.username ?? 'Unknown user',
            pictureURI: UrlUtilService.getMediaUrl(
              u.pictureURI || '/uploads/profile-pictures/default.png'
            ),
            additionalInfo: u.fullName ?? '',
            location: u.location ?? 'Unknown location',
            type: 'USER' as const,
          })),
        ])
      );
  }
}
