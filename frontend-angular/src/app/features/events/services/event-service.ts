import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, pipe, take} from 'rxjs';
import {Event} from '../models/event'
import {PageResponse} from '../../../shared/entities/page-response';
import {EventSummary} from '../models/event-summary';

@Injectable({
  providedIn: 'root'
})

export class EventService {
  private http = inject(HttpClient);

  public getAllEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint)
      .pipe(take(1));
  }

  public getAllEventsPaginated(page: number, size: number): Observable<PageResponse<EventSummary>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<EventSummary>>(`${environment.eventsEndpoint}/paginated`, { params })
      .pipe(take(1));
  }

  public getAllPublicEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint, {
      params: { isPrivate: false }
    })
      .pipe(take(1));
  }

  public getAllPrivateEvents(): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(environment.eventsEndpoint, {
      params: { isPrivate: true }
    })
      .pipe(take(1));
  }

  public getPublicEventsByUserId(userId: string): Observable<EventSummary[]> {
    return this.http.get<EventSummary[]>(`${environment.eventsEndpoint}?userId=${userId}&isPrivate=${false}`)
      .pipe(take(1));
  }

  public createNewEvent(formData: FormData): Observable<Event> {
    return this.http.post<Event>(environment.eventsEndpoint, formData)
      .pipe(take(1));
  }

  public getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.eventsEndpoint}/${id}`)
      .pipe(take(1));
  }
}
