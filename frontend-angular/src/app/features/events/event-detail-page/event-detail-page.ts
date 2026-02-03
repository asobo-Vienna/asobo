import {Component, inject, signal} from '@angular/core';
import {EventService} from '../services/event-service';
import {Event} from '../models/event';
import {ActivatedRoute} from '@angular/router';
import {DatePipe} from '@angular/common';
import {CreateComment} from "../create-comment/create-comment";
import {Participants} from '../participants/participants';
import {CommentsList} from '../comments-list/comments-list';
import {CommentService} from '../services/comment-service';
import {Comment} from '../models/comment';
import {Participant} from '../models/participant';
import {Observable} from 'rxjs';
import {Gallery} from '../gallery/gallery';
import {MediaService} from '../services/media-service';
import {MediaItem} from '../models/media-item';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';
import {AuthService} from '../../auth/services/auth-service';
import {User} from '../../auth/models/user';
import {ParticipantService} from '../services/participant-service';
import {LambdaFunctions} from '../../../shared/utils/lambda-functions';
import {environment} from '../../../../environments/environment';
import {Tag} from 'primeng/tag';
import {InputText} from 'primeng/inputtext';
import {
  AbstractControl, FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {DatePicker} from 'primeng/datepicker';
import {DateUtils} from '../../../shared/utils/date/date-utils';
import {CdkTextareaAutosize} from '@angular/cdk/text-field';

@Component({
  selector: 'app-event-detail-page',
  imports: [
    DatePipe,
    CreateComment,
    Participants,
    CommentsList,
    Gallery,
    Tag,
    InputText,
    FormsModule,
    ReactiveFormsModule,
    DatePicker,
    CdkTextareaAutosize,
  ],
  templateUrl: './event-detail-page.html',
  styleUrl: './event-detail-page.scss'
})
export class EventDetailPage {
  private route = inject(ActivatedRoute);
  private eventService = inject(EventService);
  private commentService = inject(CommentService);
  private mediaService = inject(MediaService);
  authService = inject(AuthService);
  participantService = inject(ParticipantService);

  private formBuilder = inject(FormBuilder);

  id!: string;
  title!: string;
  pictureURI!: string;
  date!: string;
  time!: string;
  location!: string;
  description?: string;
  isPrivate!: boolean;

  editEventForm: FormGroup;
  isEditing: boolean = false;

  comments = signal<List<Comment>>(new List<Comment>());
  participants = signal<List<Participant>>(new List<Participant>());
  mediaItems = signal<List<MediaItem>>(new List<MediaItem>());

  event = signal<Event | null>(null);
  currentUser: User | null = this.authService.currentUser();
  isUserAlreadyPartOfEvent = signal(false);
  protected readonly UrlUtilService = UrlUtilService;

  constructor() {
    this.editEventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(environment.minEventTitleLength), Validators.maxLength(environment.maxEventTitleLength)]],
      description: ['', [Validators.required, Validators.minLength(environment.minEventDescriptionLength), Validators.maxLength(environment.maxEventDescriptionLength)]],
      location: ['', [Validators.required]],
      date: ['', [Validators.required, this.validateDate]],
      isPrivate: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      this.loadEvent(eventId).subscribe({
        next: (event) => this.populateEvent(event),
        error: (err) => console.error('Error fetching event:', err)
      });
    }
  }

  public loadEvent(eventId: string): Observable<Event> {
    return this.eventService.getEventById(eventId);
  }

  private populateEvent(event: Event): void {
    this.event.set(event);

    this.editEventForm.patchValue({
      title: event.title,
      description: event.description,
      location: event.location,
      date: event.date ? new Date(event.date) : null,
      isPrivate: event.isPrivate
    });

    this.id = event.id;
    this.title = event.title;
    this.pictureURI = event.pictureURI;
    this.date = event.date;
    this.time = event.date;
    this.location = event.location;
    this.description = event.description;
    this.isPrivate = event.isPrivate;

    this.participantService.getAllByEventId(event.id).subscribe((participants: List<Participant>) => {
      this.participants.set(participants);
      if (this.currentUser) {
        const participant = this.participantService.mapUserToParticipant(this.currentUser);
        this.isUserAlreadyPartOfEvent.set(
          participants.contains(participant, LambdaFunctions.compareById)
        );
      }
    });

    this.commentService.getAllByEventId(event.id).subscribe((comments: List<Comment>) => {
      this.comments.set(comments);
    });
    this.mediaService.getAllByEventId(event.id).subscribe((mediaItems: List<MediaItem>) => {
      this.mediaItems.set(mediaItems);
    });
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
    this.mediaService.upload(this.id, file).subscribe({
      next: (mediaItem) => this.mediaItems().add(mediaItem),
      error: (err) => console.error('Failed to upload media!', err)
    });
  }


  public deleteMedia(item: MediaItem) {
    this.mediaItems().remove(item);       // remove immediately
    this.mediaService.delete(this.id, item).subscribe({
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

    this.participantService.joinOrLeaveEvent(this.id, this.currentUser).subscribe({
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

  public toggleEdit() {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      // reset form to the original values
      this.editEventForm.reset({
        title: this.title,
        description: this.description,
        location: this.location,
        date: this.date ? new Date(this.date) : null,
        isPrivate: this.isPrivate
      });
    }
  }

  protected isCurrentUserEventAdmin(event: Event, currentUser: User): boolean {
    return event.eventAdmins.contains(currentUser);
  }

  onSubmit() {
    if (!this.editEventForm.valid) {
      console.log('Form is invalid, stopping event submission');
      return;
    }

    const eventData = {
      ...this.editEventForm.value,
      date: DateUtils.toLocalISOString(this.editEventForm.value.date)
    };

    this.eventService.updateEvent(this.id, eventData).subscribe({
      next: (event) => {
        alert(`Event ${event.title} updated successfully!`);

        // update local properties so view mode shows correct info
        this.title = this.editEventForm.value.title;
        this.description = this.editEventForm.value.description;
        this.location = this.editEventForm.value.location;
        this.date = this.editEventForm.value.date;
        this.time = this.editEventForm.value.date;
        this.isPrivate = this.editEventForm.value.isPrivate;

        this.isEditing = false;
      },
      error: (err) => {
        console.log('Error updating event', err);
      }
    });
  }

  validateDate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const selected = new Date(control.value);
    if (selected < new Date()) {
      return { pastDate: true };
    }
    return null;
  }

  protected readonly environment = environment;
}

