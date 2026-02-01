import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import {Router, RouterLink} from '@angular/router';
import { SearchService } from '../services/search-service';
import { AutocompleteItem } from '../../../shared/entities/search';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import {of} from 'rxjs';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-global-search',
  templateUrl: './global-search.html',
  styleUrls: ['./global-search.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AutoCompleteModule],
})
export class GlobalSearch {
  private searchService = inject(SearchService);
  private router = inject(Router);

  searchControl = new FormControl<string | AutocompleteItem | null>(null);
  searchResults: AutocompleteItem[] = [];

  constructor() {
    // Listen for value changes and fetch results
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((value) => {
          let query = '';

          if (typeof value === 'string') {
            query = value;
          } else if (value && typeof value === 'object') {
            query = value.name;
          }

          if (!query || query.length < 2) {
            return of([] as AutocompleteItem[]);
          }

          return this.searchService.search(query);
        })
      )
      .subscribe((results) => {
        console.log('searchResults array?', results, Array.isArray(results));
        this.searchResults = results;
      });
  }

  onSelect(event: any) {
    const item = event.value as AutocompleteItem;
    if (item.type === 'EVENT') {
      this.router.navigate(['/events', item.id]);
    } else if (item.type === 'USER') {
      this.router.navigate([UrlUtilService.getUserRouterLink(item.username)]);
    }
  }

  // Optional: display function for autocomplete
  displayFn(item?: AutocompleteItem): string {
    return item ? item.name : '';
  }

  protected readonly UrlUtilService = UrlUtilService;
}
