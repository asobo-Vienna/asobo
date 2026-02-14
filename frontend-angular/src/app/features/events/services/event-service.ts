import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Event} from '../models/event'
import {PageResponse} from '../../../shared/entities/page-response';
import {EventSummary} from '../models/event-summary';
import {EventFilters} from '../models/event-filters';

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

  getAllPublicEventsPaginated(params: { page: number, size: number, sort: string }): Observable<PageResponse<EventSummary>> {
    const queryParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sort)
      .set('isPrivate', 'false');

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, { params: queryParams });
  }

  public getPublicEventsByUserId(userId: string): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}?userId=${userId}&isPrivate=${false}`)
  }

  public getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.eventsEndpoint}/${id}`);
  }

  public createNewEvent(eventData: Partial<Event>): Observable<Event> {
    return this.http.post<Event>(environment.eventsEndpoint, eventData);
  }

  public uploadEventPicture(eventId: string, formData: FormData): Observable<Event> {
    return this.http.patch<Event>(`${environment.eventsEndpoint}/${eventId}/picture`, formData);
  }
}
