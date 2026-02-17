import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Event} from '../models/event'
import {PageResponse} from '../../../shared/entities/page-response';
import {EventSummary} from '../models/event-summary';
import {EventFilters} from '../models/event-filters';
import {List} from '../../../core/data-structures/lists/list';
import {User} from '../../auth/models/user';

@Injectable({
  providedIn: 'root'
})

export class EventService {
  private http = inject(HttpClient);

  public getAllEvents(eventFilters?: EventFilters): Observable<EventSummary[]> {
    let params: HttpParams = new HttpParams();
    if (eventFilters) {
      params = this.filtersToHttpParams(eventFilters);
    }

    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}`, { params });
  }

  private filtersToHttpParams(filters: EventFilters): HttpParams {
    let params = new HttpParams();

    Object.keys(filters).forEach(key => {
      const value = filters[key as keyof EventFilters];
      if (value !== null && value !== undefined) {
        if (value instanceof Date) {
          params = params.set(key, value.toISOString());
        } else {
          params = params.set(key, String(value));
        }
      }
    });

    return params;
  }

  getAllEventsPaginated(params: { page: number, size: number, sort?: string }, eventFilters: EventFilters): Observable<PageResponse<EventSummary>> {
    const queryParams = this.filtersToHttpParams(eventFilters)
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sort ?? 'date,desc');

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, { params: queryParams });
  }

  searchEvents(
    query?: string,
    location?: string,
    dateFrom?: string,
    dateTo?: string,
    page: number = 0,
    size: number = 20
  ): Observable<PageResponse<EventSummary>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (query) params = params.set('query', query);
    if (location) params = params.set('location', location);
    if (dateFrom) params = params.set('dateFrom', dateFrom);
    if (dateTo) params = params.set('dateTo', dateTo);

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, { params });
  }

  public getPublicEventsByUserId(userId: string): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}?userId=${userId}&isPrivate=${false}`)
  }

  public getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.eventsEndpoint}/${id}`)
      .pipe(map(event => this.convertEventAdminsToList(event))
    );
  }

  public createNewEvent(eventData: Partial<Event>): Observable<Event> {
    return this.http.post<Event>(environment.eventsEndpoint, eventData);
  }

  public uploadEventPicture(eventId: string, formData: FormData): Observable<Event> {
    return this.http.patch<Event>(`${environment.eventsEndpoint}/${eventId}/picture`, formData);
  }

  public updateEvent(eventId: string, eventData: Partial<Event>): Observable<Event> {
    return this.http.patch<Event>(`${environment.eventsEndpoint}/${eventId}`, eventData)
      .pipe(map(event => this.convertEventAdminsToList(event))
    );
  }

  public deleteEvent(eventId: string): Observable<Event> {
    return this.http.delete<Event>(`${environment.eventsEndpoint}/${eventId}`);
  }

  public convertEventAdminsToList(event: Event): Event {
    return {
      ...event,
      eventAdmins: new List<User>(event.eventAdmins as unknown as User[])
    };
  }
}
