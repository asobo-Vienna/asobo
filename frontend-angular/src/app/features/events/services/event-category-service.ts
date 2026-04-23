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

  getCategoryColor(index: number): string {
    // each new color fills the largest remaining gap on the color wheel,
    // so colors stay as visually distinct from each other as possible
    const hue = (index * 137.5) % 360;
    return `hsl(${hue}, 80%, 45%)`;
  }
}
