import {Component, inject, signal} from '@angular/core';
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {environment} from '../../../../environments/environment';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AdminService} from '../services/admin-service';
import {MediaItem} from '../../events/models/media-item';

@Component({
  selector: 'app-admin-media-list',
    imports: [
        PrimeTemplate,
        TableModule
    ],
  templateUrl: './admin-media-list.html',
  styleUrl: './admin-media-list.scss',
})
export class AdminMediaList {
  private adminService = inject(AdminService);
  mediaItems = signal<MediaItem[]>([]);

  ngOnInit(): void {
    this.adminService.getAllMedia().subscribe({
      next: (mediaItems) => {
        this.mediaItems.set(mediaItems);
      },
      error: (err) => console.error('Error fetching media:', err)
    });
    return;
  }

  onEdit(mediaItem: any) {
    console.log('Editing media item:', mediaItem);
  }

  onDelete(mediaItems: any) {
    console.log('Deleting media item:', mediaItems);
  }

  getUserRouterLink(username: string): string {
    return `${environment.userProfileBaseUrl}${username}`;
  }

  protected readonly environment = environment;
  protected readonly UrlUtilService = UrlUtilService;
}
