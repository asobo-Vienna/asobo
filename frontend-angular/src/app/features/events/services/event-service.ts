import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Event} from '../models/event'
import {PageResponse} from '../../../shared/entities/page-response';
import {EventSummary} from '../models/event-summary';

@Injectable({
  providedIn: 'root'
})

export class EventService {
  private http = inject(HttpClient);

  public getAllEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint);
  }

  public getAllEventsPaginated(page: number, size: number): Observable<PageResponse<EventSummary>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, { params });
  }

  public getAllPublicEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint, {
      params: { isPrivate: false }
    });
  }

  public getAllPrivateEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint, {
      params: { isPrivate: true }
    });
  }

  public getPublicEventsByUserId(userId: string): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}?userId=${userId}&isPrivate=${false}`)
  }

  public createNewEvent(formData: FormData): Observable<Event> {
    return this.http.post<Event>(environment.eventsEndpoint, formData);
  }

  public getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.eventsEndpoint}/${id}`);
  }
}
