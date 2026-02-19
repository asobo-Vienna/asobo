import {inject, Injectable} from '@angular/core';
import {MessageService} from 'primeng/api';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private messageService = inject(MessageService);

  error(message: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
