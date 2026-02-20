import {inject, Injectable} from '@angular/core';
import {MessageService} from 'primeng/api';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private messageService = inject(MessageService);

  info(message: string) {
    this.messageService.add({ severity: 'info', summary: 'Info', detail: message });
  }

  success(message: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: message });
  }

  warn(message: string) {
    this.messageService.add({ severity: 'warn', summary: 'Warning', detail: message });
  }

  error(message: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
  }
}
