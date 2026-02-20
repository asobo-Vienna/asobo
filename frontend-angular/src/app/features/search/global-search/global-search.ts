import {Component, inject, input, output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {AutoCompleteCompleteEvent, AutoCompleteModule} from 'primeng/autocomplete';
import {Router} from '@angular/router';
import {SearchService} from '../services/search-service';
import {AutocompleteItem} from '../../../shared/entities/search';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AuthService} from '../../auth/services/auth-service';
import {ToastService} from '../../../shared/services/toast-service';

@Component({
  selector: 'app-global-search',
  templateUrl: './global-search.html',
  styleUrls: ['./global-search.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AutoCompleteModule],
})
export class GlobalSearch {
  searchType = input<'all' | 'events' | 'simple'>('all');
  searchOutput = output<string>();

  private searchService = inject(SearchService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  searchControl = new FormControl<string | AutocompleteItem | null>(null);
  searchResults: AutocompleteItem[] = [];

  search(event: AutoCompleteCompleteEvent) {
    const query = event.query;
    if (!query || query.length < 2) {
      this.searchResults = [];
      return;
    }

    this.searchService.search(query, this.authService.isLoggedIn()).subscribe((results) => {
      this.searchResults = this.searchType() === 'events'
        ? results.filter(item => item.type === 'EVENT')
        : results;
    });
  }

  onSimpleSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    const query = input.value;

    this.searchOutput.emit(query);
  }

  onSelect(event: any) {
    const item = event.value as AutocompleteItem;
    if (item.type === 'EVENT') {
      this.router.navigate(['/events', item.id]);
    } else if (item.type === 'USER') {
      this.router.navigate([UrlUtilService.getUserRouterLink(item.username)]);
    }
  }

  triggerSearch() {
    const value = this.searchControl.value;
    let query = '';
    if (typeof value === 'string') {
      query = value;
    } else if (value && typeof value === 'object') {
      query = value.name;
    }

    if (query && query.trim().length >= 2) {
      this.router.navigate(['/search'], {queryParams: {q: query}});
    } else {
      this.toastService.warn("Minimum of 2 characters required for search.");
    }
  }

  protected readonly UrlUtilService = UrlUtilService;
}
