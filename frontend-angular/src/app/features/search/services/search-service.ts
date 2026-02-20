import {Injectable, inject} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {
  AutocompleteItem,
  EventSearchResult,
  GlobalSearchResponse,
  UserSearchResult,
  SearchResultsResponse
} from '../../../shared/entities/search';
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {EventSummary} from '../../events/models/event-summary';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private http = inject(HttpClient);

  search(query: string, includePrivate: boolean): Observable<AutocompleteItem[]> {
    let params = new HttpParams()
      .set('query', query)
      .set('includePrivate', includePrivate.toString());

    return this.http
      .get<GlobalSearchResponse>(`${environment.apiBaseUrl}/search`, { params })
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

fullSearch(query: string, includePrivate: boolean): Observable<SearchResultsResponse> {
  let params = new HttpParams()
    .set('query', query)
    .set('includePrivate', includePrivate.toString());

  return this.http
    .get<GlobalSearchResponse>(`${environment.apiBaseUrl}/search`, { params })
    .pipe(
      map((response) => ({
        events: response.events.map((e: EventSearchResult): EventSummary => ({
          id: e.id,
          title: e.title ?? 'Untitled event',
          description: e.description ?? '',
          pictureURI: e.pictureURI || UrlUtilService.getMediaUrl(environment.eventDummyCoverPicRelativeUrl),
          date: e.date ?? '',
          time: e.date ? new Date(e.date).toLocaleTimeString('de-AT', {
            hour: '2-digit',
            minute: '2-digit'
          }) : '',
          location: e.location ?? 'Unknown location',
          isPrivateEvent: e.isPrivateEvent ?? false,
          participantCount: e.participantCount ?? 0,
          commentCount: 0,
          mediaCount: 0,
        })),
        users: response.users.map((u: UserSearchResult) => ({
          id: u.id,
          username: u.username ?? 'Unknown user',
          pictureURI: UrlUtilService.getMediaUrl(
            u.pictureURI || '/uploads/profile-pictures/default.png'
          ),
          fullName: u.fullName ?? '',
          location: u.location ?? 'Unknown location',
        })),
        totalResults: response.totalResults,
      }))
    );
}
}
