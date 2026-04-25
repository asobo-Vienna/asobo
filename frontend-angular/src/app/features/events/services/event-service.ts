import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Event} from '../../../shared/entities/events/event'
import {PageResponse} from '../../../shared/entities/page-response';
import {EventSummary} from '../../../shared/entities/events/event-summary';
import {EventFilters} from '../../../shared/entities/filters/event-filters';
import {User} from '../../../shared/entities/users/user';
import {UserBasic} from '../../../shared/entities/users/user-basic';
import {List} from '../../../core/data-structures/lists/list';

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

    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}`, {params});
  }

  private filtersToHttpParams(filters: EventFilters): HttpParams {
    let params = new HttpParams();

    Object.keys(filters).forEach(key => {
      const value = filters[key as keyof EventFilters];
      if (value !== null && value !== undefined) {
        if (value instanceof Date) {
          params = params.set(key, value.toISOString());
        } else if (value instanceof List) {
          value.toArray().forEach((item: any) => {
            params = params.append(key, String(item));
          });
        } else {
          params = params.set(key, String(value));
        }
      }
    });

    return params;
  }

  getAllEventsPaginated(params: {
    page: number,
    size: number,
    sort?: string
  }, eventFilters: EventFilters): Observable<PageResponse<EventSummary>> {
    const queryParams = this.filtersToHttpParams(eventFilters)
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sort ?? 'date,desc');

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, {params: queryParams});
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

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, {params});
  }

  public getEventsPaginated(params: {
    page: number,
    size: number,
    sort?: string
  }, eventFilters: EventFilters): Observable<PageResponse<EventSummary>> {
    const queryParams = this.filtersToHttpParams(eventFilters)
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sort ?? 'date,desc');

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, {params: queryParams});
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

  public removeEventPicture(eventId: string): Observable<void> {
    return this.http.delete<void>(`${environment.eventsEndpoint}/${eventId}/picture`);
  }

  public updateEvent(eventId: string, eventData: Partial<Event>): Observable<Event> {
    return this.http.patch<Event>(`${environment.eventsEndpoint}/${eventId}`, eventData);
  }

  public deleteEvent(eventId: string): Observable<Event> {
    return this.http.delete<Event>(`${environment.eventsEndpoint}/${eventId}`);
  }

  public addEventAdmins(eventId: string, eventAdmins: User[]): Observable<UserBasic[]> {
    const userIds = eventAdmins.map(u => u.id);
    return this.http.patch<UserBasic[]>(
      `${environment.eventsEndpoint}/${eventId}/admins`,
      userIds
    );
  }

  public removeEventAdmins(eventId: string, eventAdmins: User[]): Observable<UserBasic[]> {
    const userIds = eventAdmins.map(u => u.id);
    return this.http.delete<UserBasic[]>(`${environment.eventsEndpoint}/${eventId}/admins`, {
      body: userIds
    });
  }

  public exportEvent(eventId: string): void {
    this.http.get(`${environment.eventsEndpoint}/${eventId}/export`, {responseType: 'blob'})
      .subscribe(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `event_${eventId}.ics`;
        a.click();
        URL.revokeObjectURL(url);
      });
  }
}
