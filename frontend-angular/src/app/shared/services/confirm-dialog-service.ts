import {inject, Injectable} from '@angular/core';
import {ConfirmationService} from 'primeng/api';
import {EntityType} from '../types/entity-type';

type Severity = 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast';

type BaseEntityConfig = {
  label: string;
  icon: string;
  deleteMessage: (name?: string) => string;
};

type ReactivableEntityConfig = BaseEntityConfig & {
  reactivateMessage: (name?: string) => string;
};

type EntityConfig = BaseEntityConfig | ReactivableEntityConfig;

const ENTITY_CONFIG = {
  user: {
    label: 'User',
    icon: 'pi pi-user',
    deleteMessage: (name) =>
      name ? `Delete user ${name}?` : 'Delete this user?',
    reactivateMessage: (name) =>
      name ? `Reactivate user ${name}?` : 'Reactivate this user?'
  },

  event: {
    label: 'Event',
    icon: 'pi pi-calendar',
    deleteMessage: (name) =>
      name ? `Are you sure that you want to delete event ${name}?` : 'Are you sure that you want to delete this event?',
    reactivateMessage: (name) =>
      name ? `Are you sure that you want to reactivate event ${name}?` : 'Are you sure that you want to reactivate this event?'
  },

  comment: {
    label: 'Comment',
    icon: 'pi pi-comment',
    deleteMessage: (name) =>
      name
        ? name.length > 30
          ? `Are you sure that you want to delete comment ${name.slice(0, 30)}...?`
          : `Are you sure that you want to delete comment ${name}?`
        : 'Are you sure that you want to delete this comment?'
  },

  medium: {
    label: 'Medium',
    icon: 'pi pi-folder',
    deleteMessage: (name) => 'Are you sure that you want to delete this medium?'
  },

  profilePicture: {
    label: 'Profile Picture',
    icon: 'pi pi-user',
    deleteMessage: (name) =>
      name ? `Are you sure that you want to delete profile picture of ${name}?` : 'Are you sure that you want to delete this profile picture?'
  },

  coverPicture: {
    label: 'Cover Picture',
    icon: 'pi pi-image',
    deleteMessage: (name) =>
      name ? `Are you sure that you want to delete cover picture of event ${name}?` : 'Are you sure that you want to delete this cover picture?'
  }
} satisfies Record<EntityType, EntityConfig>;

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {
  private confirmationService = inject(ConfirmationService);

  confirmDelete(type: EntityType, name?: string): Promise<boolean> {
    const config = ENTITY_CONFIG[type];

    return this.openConfirm({
      message: config.deleteMessage(name),
      header: `Delete ${config.label}`,
      icon: config.icon,
      acceptLabel: 'Delete',
      severity: 'danger'
    });
  }

  confirmSave(): Promise<boolean> {
    return this.openConfirm({
      message: 'Do you want to save your changes?',
      header: 'Save Changes',
      icon: 'pi pi-check',
      acceptLabel: 'Save',
      severity: 'success'
    });
  }

  // only user can call this at this point (compile-time enforced)
  confirmReactivate(
    type: Extract<EntityType, 'user' | 'event'>,
    name?: string
  ): Promise<boolean> {
    const config = ENTITY_CONFIG[type] as ReactivableEntityConfig;

    return this.openConfirm({
      message: config.reactivateMessage(name),
      header: `Reactivate ${config.label}`,
      icon: config.icon,
      acceptLabel: 'Reactivate',
      severity: 'success'
    });
  }

  private openConfirm(config: {
    message: string;
    header: string;
    icon: string;
    acceptLabel: string;
    severity: Severity;
  }): Promise<boolean> {
    return new Promise((resolve) => {
      this.confirmationService.confirm({
        message: config.message,
        header: config.header,
        icon: config.icon,
        acceptButtonProps: {
          label: config.acceptLabel,
          severity: config.severity
        },
        rejectButtonProps: {
          label: 'Cancel',
          severity: 'secondary',
          outlined: true
        },
        accept: () => resolve(true),
        reject: () => resolve(false)
      });
    });
  }
}
