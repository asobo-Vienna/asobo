import {Component, inject, OnInit, signal} from '@angular/core';
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
  ],
  templateUrl: './event-detail-page.html',
  styleUrl: './event-detail-page.scss'
})
export class EventDetailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private eventService = inject(EventService);
  private commentService = inject(CommentService);
  private mediaService = inject(MediaService);
  authService = inject(AuthService);
  participantService = inject(ParticipantService);

  event = signal<Event | null>(null);
  comments = signal<List<Comment>>(new List<Comment>());
  participants = signal<List<Participant>>(new List<Participant>());
  mediaItems = signal<List<MediaItem>>(new List<MediaItem>());

  isUserAlreadyPartOfEvent = signal(false);

  currentUser: User | null = this.authService.currentUser();

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
        error: err => console.error('Error fetching event:', err)
      });
  }


  public loadEvent(eventId: string): Observable<Event> {
    return this.eventService.getEventById(eventId);
  }


  private populateEvent(event: Event): void {
    this.event.set(this.eventService.convertEventAdminsToList(event));

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
        console.error('Failed to delete comment!', err);
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
        console.error('Failed to edit comment!', err);
      }
    });
  }


  public uploadMedia(file: File) {
    const currentEvent = this.event();
    if (!currentEvent) return;

    this.mediaService.upload(currentEvent.id, file).subscribe({
      next: (mediaItem) => this.mediaItems().add(mediaItem),
      error: (err) => console.error('Failed to upload media!', err)
    });
  }


  public deleteMedia(item: MediaItem) {
    const currentEvent = this.event();
    if (!currentEvent) return;

    this.mediaItems().remove(item);       // remove immediately
    this.mediaService.delete(currentEvent.id, item).subscribe({
      error: (err) => {
        console.error('Failed to delete media!', err);
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
        alert(err.error.message);
        console.error('Error joining event:', err);
      }
    });
  }


  public onEventUpdated(updatedEvent: Event) {
    this.event.set(this.eventService.convertEventAdminsToList(updatedEvent));
  }
}

