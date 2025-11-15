import {inject, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Event} from '../models/event'

@Injectable({
  providedIn: 'root'
})

export class EventService {
  private http = inject(HttpClient);

  public getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(environment.eventsEndpoint);
  }

  public createNewEvent(formData: FormData): Observable<Event> {
    return this.http.post<Event>(environment.eventsEndpoint, formData);
  }

  public getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${environment.eventsEndpoint}/${id}`);
  }
}
