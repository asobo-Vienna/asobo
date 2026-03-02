import {AfterViewChecked, Component, ElementRef, inject, OnInit, QueryList, signal, ViewChildren} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {SearchService} from '../services/search-service';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AuthService} from '../../auth/services/auth-service';
import {EventCard} from '../../events/event-card/event-card';
import {EventSummary} from '../../events/models/event-summary';
import {Spinner} from '../../../core/ui-elements/spinner/spinner';
import {UserSearchResultBasic} from '../../../shared/entities/search';
import {SecureImagePipe} from '../../../core/pipes/secure-image-pipe';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-search-results-page',
  imports: [RouterLink, Spinner, EventCard, SecureImagePipe, AsyncPipe],
  templateUrl: './search-results-page.html',
  styleUrl: './search-results-page.scss'
})
export class SearchResultsPage implements OnInit, AfterViewChecked {
  private route = inject(ActivatedRoute);
  private searchService = inject(SearchService);
  private authService = inject(AuthService);

  searchQuery = signal<string>('');
  events = signal<EventSummary[]>([]);
  users = signal<UserSearchResultBasic[]>([]);
  loading = signal<boolean>(false);

  @ViewChildren('userCard') userCards!: QueryList<ElementRef>;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const query = params['q'];
      if (query && query.trim().length >= 2) {
        this.searchQuery.set(query);
        this.performSearch(query);
      }
    });
  }

  private performSearch(query: string) {
    this.loading.set(true);

    this.searchService.fullSearch(query, this.authService.isLoggedIn()).subscribe({
      next: (response) => {
        this.events.set(response.events as EventSummary[]);
        this.users.set(response.users);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Search error:', err);
        this.loading.set(false);
      }
    });
  }

  ngAfterViewChecked(): void {
    const cards = this.userCards.toArray();
    if (cards.length === 0) return;
    cards.forEach(c => c.nativeElement.style.height = 'auto');
    const maxHeight = Math.max(...cards.map(c => c.nativeElement.offsetHeight));
    cards.forEach(c => c.nativeElement.style.height = maxHeight + 'px');
  }

  getUserLink(username: string): string {
    return UrlUtilService.getUserRouterLink(username);
  }

  protected readonly UrlUtilService = UrlUtilService;
}
