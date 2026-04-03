import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {EventCategory} from '../../../shared/entities/events/event-category';

@Injectable({
  providedIn: 'root'
})

export class EventCategoryService {
  private http = inject(HttpClient);

  public getAllCategories(): Observable<EventCategory[]> {
    return this.http.get<EventCategory[]>(`${environment.eventCategoriesEndpoint}`);
  }
}
