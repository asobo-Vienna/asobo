import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {EventService} from '../services/event-service';
import {Event} from '../models/event';
import {ActivatedRoute} from '@angular/router';
import {CreateComment} from "../create-comment/create-comment";
import {Participants} from '../participants/participants';
import {CommentsList} from '../comments-list/comments-list';
import {CommentService} from '../services/comment-service';
import {Comment} from '../models/comment';
import {Participant} from '../models/participant';
import {distinctUntilChanged, filter, map, Observable, switchMap} from 'rxjs';
import {Gallery} from '../gallery/gallery';
import {MediaService} from '../services/media-service';
import {MediaItem} from '../models/media-item';
import {List} from '../../../core/data-structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AuthService} from '../../auth/services/auth-service';
import {User} from '../../auth/models/user';
import {ParticipantService} from '../services/participant-service';
import {LambdaFunctions} from '../../../shared/utils/lambda-functions';
import {environment} from '../../../../environments/environment';
import {Tag} from 'primeng/tag';
import {PageResponse} from '../../../shared/entities/page-response';

import {
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {EventBasicInfo} from './event-basic-info/event-basic-info';
import {EventAdmins} from '../event-admins/event-admins';
import {AccessControlService} from '../../../shared/services/access-control-service';
import {PictureUpload} from '../../../core/picture-upload/picture-upload';
import {ToastService} from '../../../shared/services/toast-service';

@Component({
  selector: 'app-event-detail-page',
  imports: [
    CreateComment,
    Participants,
    CommentsList,
    Gallery,
    Tag,
    FormsModule,
    ReactiveFormsModule,
    EventBasicInfo,
    EventAdmins,
    PictureUpload,
  ],
  templateUrl: './event-detail-page.html',
  styleUrl: './event-detail-page.scss'
})
export class EventDetailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private toastService = inject(ToastService);
  private eventService = inject(EventService);
  private commentService = inject(CommentService);
  private mediaService = inject(MediaService);
  participantService = inject(ParticipantService);
  authService = inject(AuthService);
  accessControlService = inject(AccessControlService);

  event = signal<Event | null>(null);

  displayImage = computed(() => UrlUtilService.getMediaUrl(this.event()?.pictureURI || environment.eventDummyCoverPicRelativeUrl));
  previewImage = signal<string | null>(null);
  selectedImage: File | null = null;

  comments = signal<List<Comment>>(new List<Comment>());
  participants = signal<List<Participant>>(new List<Participant>());
  mediaItems = signal<List<MediaItem>>(new List<MediaItem>());

  currentUser: User | null = this.authService.currentUser();
  isUserAlreadyPartOfEvent = signal(false);

  isCurrentUserAdmin = computed(() =>
    this.accessControlService.hasAdminAccess());
  isCurrentUserEventAdmin = computed(() =>
    this.accessControlService.isCurrentUserEventAdmin(this.event())
  );

  protected readonly UrlUtilService = UrlUtilService;
  protected readonly environment = environment;

  ngOnInit(): void {
      this.route.paramMap.pipe(
        map(params => params.get('id')),
        filter((id): id is string => id !== null),
        distinctUntilChanged(),
        switchMap(id => this.loadEvent(id))
      ).subscribe({
        next: event => this.populateEvent(event),
        error: err => {
          console.log(err);
          this.toastService.error('Error fetching event!')
        }
      });
  }


  public loadEvent(eventId: string): Observable<Event> {
    return this.eventService.getEventById(eventId);
  }


  private populateEvent(event: Event): void {
    this.event.set(event);

    if (this.authService.isLoggedIn()) {

      this.participantService.getAllByEventId(event.id).subscribe((participants: List<Participant>) => {
        this.participants.set(participants);
        if (this.currentUser) {
          const participant = this.participantService.mapUserToParticipant(this.currentUser);
          this.isUserAlreadyPartOfEvent.set(
            participants.contains(participant, LambdaFunctions.compareById)
          );
        }
      });

      this.commentService.getAllByEventId(event.id).subscribe((comments: PageResponse<Comment>) => {
        this.comments.set(new List(comments.content));
      });

      this.mediaService.getAllByEventId(event.id).subscribe((mediaItems: List<MediaItem>) => {
        this.mediaItems.set(mediaItems);
      });
    }
  }


  public onCommentCreated(comment: Comment) {
    if (this.currentUser === null) {
      return;
    }
    comment.authorId = this.currentUser.id;
    comment.username = this.currentUser.username;
    comment.pictureURI = this.currentUser.pictureURI;
    this.comments().add(comment);
  }


  public deleteComment(comment: Comment) {
    this.commentService.delete(comment).subscribe({
      next: () => {
        this.comments().remove(comment);
      },
      error: (err) => {
        console.log(err);
        this.toastService.error('Failed to delete comment!');
      }
    });
  }


  public editComment(comment: Comment) {
    this.commentService.edit(comment).subscribe({
      next: (updatedComment) => {
        const index = this.comments().findIndex(updatedComment, LambdaFunctions.compareById);
        if (index !== -1) {
          this.comments().set(index, updatedComment);
        }
      },
      error: (err) => {
        console.log(err);
        this.toastService.error('Failed to edit comment!');
      }
    });
  }


  public uploadMedia(file: File) {
    const currentEvent = this.event();
    if (!currentEvent) return;

    this.mediaService.upload(currentEvent.id, file).subscribe({
      next: (mediaItem) => this.mediaItems().add(mediaItem),
      error: (err) => {
        console.log(err);
        this.toastService.error('Failed to upload media!');
      }
    });
  }


  public deleteMedia(item: MediaItem) {
    const currentEvent = this.event();
    if (!currentEvent) return;

    this.mediaItems().remove(item);       // remove immediately
    this.mediaService.delete(currentEvent.id, item).subscribe({
      error: (err) => {
        console.log(err);
        this.toastService.error('Failed to delete media!');
        this.mediaItems().add(item);     // revert if backend fails
      }
    });
  }


  public joinOrLeaveEvent() {
    if (!this.currentUser) {
      return;
    }

    const currentEvent = this.event();
    if (!currentEvent) return;

    this.participantService.joinOrLeaveEvent(currentEvent.id, this.currentUser).subscribe({
      next: (participants: List<Participant>) => {
        if (!this.currentUser) {
          return;
        }

        const participantToJoin = this.participantService.mapUserToParticipant(this.currentUser);
        this.participants.set(participants);

        // compare by ID function passed into List.contains() method
        this.isUserAlreadyPartOfEvent.set(
          this.participants().contains(participantToJoin, LambdaFunctions.compareById)
        );
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Error joining event!');
      }
    });
  }


  public onEventUpdated(updatedEvent: Event) {
    this.event.set(updatedEvent);
  }


  handleFileSelected(file: File) {
    if (!this.isAdminOrEventAdmin()) {
      this.toastService.error('You are not allowed to edit the event\'s picture');
      return;
    }

    this.selectedImage = file;
    const formData = new FormData();
    formData.append('eventPicture', file);

    const reader = new FileReader();
    reader.onload = (e) => {
      this.previewImage.set(e.target?.result as string);
    };
    reader.readAsDataURL(file);

    const currentEvent = this.event()!;

    this.eventService.uploadEventPicture(currentEvent.id, formData).subscribe({
      next: () => {
        this.event.set({
          ...currentEvent,
          pictureURI: URL.createObjectURL(this.selectedImage!)
        });

        this.toastService.success('Event picture updated');
        this.selectedImage = null;
        this.previewImage.set(null);
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Failed to upload event picture');
      }
    });
  }

  isAdminOrEventAdmin() {
    return this.isCurrentUserAdmin() || this.isCurrentUserEventAdmin();
  }
}

