import {User} from '../../auth/login/models/user';
import {map, Observable} from 'rxjs';
import {Participant} from '../models/participant';
import {environment} from '../../../../environments/environment';
import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {List} from '../../../core/data_structures/lists/list';

@Injectable({
  providedIn: 'root'
})
export class ParticipantService {

  private http = inject(HttpClient);

  getAllByEventId(eventId: string): Observable<List<Participant>> {
    return this.http
      .get<Participant[]>(this.getEventParticipantsUrl(eventId))
      .pipe(map(participants => new List<Participant>(participants)));
  }

  public joinEvent(eventId: string, user: User): Observable<Participant> {
    return this.http.post<Participant>(this.getEventParticipantsUrl(eventId), user);
  }

  private getEventParticipantsUrl(eventId: string): string {
    return `${environment.eventsAddress}/${eventId}/participants`;
  }
}
