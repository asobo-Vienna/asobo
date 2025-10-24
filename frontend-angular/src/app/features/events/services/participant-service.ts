import {User} from '../../auth/models/user';
import {map, Observable} from 'rxjs';
import {Participant} from '../models/participant';
import {environment} from '../../../../environments/environment';
import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {List} from '../../../core/data_structures/lists/list';
import {Comment} from '../models/comment';

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

  joinOrLeaveEvent(eventId: string, user: User): Observable<List<Participant>> {
    const allParticipants = this.http.post<Participant[]>(this.getEventParticipantsUrl(eventId), user)
      .pipe(map(participants => new List<Participant>(participants)));
    return allParticipants;
  }

  private getEventParticipantsUrl(eventId: string): string {
    return `${environment.eventsAddress}/${eventId}/participants`;
  }
}
