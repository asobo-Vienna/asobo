import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {AutocompleteItem, GlobalSearchResponse} from '../../../shared/entities/search';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private http = inject(HttpClient);

  search(query: string): Observable<AutocompleteItem[]> {
    return this.http.get<AutocompleteItem[]>(`${environment.apiBaseUrl}/search?q=${encodeURIComponent(query)}`);
  }
}
