import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {
  AutocompleteItem,
  EventSearchResult,
  GlobalSearchResponse,
  UserSearchResult
} from '../../../shared/entities/search';
import {environment} from '../../../../environments/environment';

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
            name: e.title,
            type: 'EVENT' as const,
          })),
          ...response.users.map((u: UserSearchResult) => ({
            ...u,
            name: `${u.username} (${u.firstName} ${u.surname})`,
            type: 'USER' as const,
          })),
        ])
      );
  }
}
