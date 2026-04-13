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

  private readonly CATEGORY_COLORS = [
    'orangered', 'deeppink', 'lightgreen', 'orange',
    'purple', 'seagreen', 'darkcyan', 'lightslategray',
    'steelblue', 'crimson', 'mediumorchid', 'goldenrod', 'cadetblue'
  ];

  getCategoryColor(id: number, name: string): string {
    if (id < this.CATEGORY_COLORS.length) {
      return this.CATEGORY_COLORS[id % this.CATEGORY_COLORS.length];
    }
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const hue = Math.abs(hash) % 360;
    return `hsl(${hue}, 65%, 45%)`;
  }

}
