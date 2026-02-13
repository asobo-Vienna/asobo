import {Component, EventEmitter, inject, input, Input, Output, ViewEncapsulation} from '@angular/core';
import {MediaItem} from '../models/media-item';
import {List} from "../../../core/data-structures/lists/list";
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {Carousel} from 'primeng/carousel';
import {PrimeTemplate} from 'primeng/api';
import {MediaUtilService} from '../../../shared/utils/media/media-util-service';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {Event} from '../models/event';
import {User} from '../../auth/models/user';

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.html',
  styleUrl: './gallery.scss',
  encapsulation: ViewEncapsulation.None,
  imports: [
    Carousel,
    PrimeTemplate
  ]
})

export class Gallery {
  protected accessControlService = inject(AccessControlService);

  @Input() mediaItems: List<MediaItem> = new List([]);
  @Output() mediaAdded = new EventEmitter<File>();
  @Output() mediaDeleted = new EventEmitter<MediaItem>();

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly MediaUtilService = MediaUtilService;

  event = input<Event | null>(null);
  currentUser = input<User | null>(null);

  showCarousel = false;
  activeSlideIndex = 0;

  openCarousel(index: number) {
    this.activeSlideIndex = index;
    this.showCarousel = true;
  }

  closeCarousel() {
    this.showCarousel = false;
  }

  onFileSelected(event: globalThis.Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.mediaAdded.emit(file);
    input.value = '';
  }
}
