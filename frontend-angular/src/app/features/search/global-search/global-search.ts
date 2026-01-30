import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { Router } from '@angular/router';
import { SearchService } from '../services/search-service';
import { AutocompleteItem } from '../../../shared/entities/search';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

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

  searchControl = new FormControl<AutocompleteItem | null>(null);
  searchResults: AutocompleteItem[] = [];

  constructor() {
    // Listen for value changes and fetch results
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((value) => {
          if (!value || typeof value === 'string') return this.searchService.search(value || '');
          return this.searchService.search(value.name || '');
        })
      )
      .subscribe((results) => {
        this.searchResults = results;
      });
  }

  onSelect(event: any) {
    const item = event.value as AutocompleteItem;
    if (item.type === 'EVENT') {
      this.router.navigate(['/events', item.id]);
    } else if (item.type === 'USER') {
      this.router.navigate(['/users', item.id]);
    }
  }

  // Optional: display function for autocomplete
  displayFn(item?: AutocompleteItem): string {
    return item ? item.name : '';
  }
}
