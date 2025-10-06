import {Component} from '@angular/core';
import {EventService} from '../event-service';
import {Event} from '../models/event';
import {ActivatedRoute} from '@angular/router';
import {DatePipe} from '@angular/common';
import {NewComment} from "../new-comment/new-comment";
import {Participants} from '../participants/participants';
import {CommentsList} from '../comments-list/comments-list';
import {CommentService} from '../comment-service';
import {Comment} from '../models/comment';
import {Participant} from '../models/participant';
import {Observable} from 'rxjs';
import {Gallery} from '../gallery/gallery';
import {MediaService} from '../media-service';
import {MediaItem} from '../models/media-item';
import {List} from '../../../core/data_structures/lists/list';
import {UrlUtilService} from '../../../shared/utils/url/url-util-service';

@Component({
  selector: 'app-event-detail-page',
  imports: [
    DatePipe,
    NewComment,
    Participants,
    CommentsList,
    Gallery,
  ],
  templateUrl: './event-detail-page.html',
  styleUrl: './event-detail-page.scss'
})
export class EventDetailPage {
  id!: string;
  title!: string;
  pictureURI!: string;
  date!: string;
  time!: string;
  location!: string;
  description?: string;
  comments: List<Comment> = new List<Comment>([]);
  participants: List<Participant> = new List<Participant>([]);
  mediaItems: List<MediaItem> = new List<MediaItem>([]);
  protected readonly UrlUtilService = UrlUtilService;

  constructor(private route: ActivatedRoute,
              private eventService: EventService,
              private commentService: CommentService,
              private mediaService: MediaService) {
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

  loadEvent(eventId: string): Observable<Event> {
    return this.eventService.getEventById(eventId);
  }

  private populateEvent(event: Event): void {
    this.id = event.id;
    this.title = event.title;
    this.pictureURI = event.pictureURI;
    this.date = event.date;
    this.time = event.date;
    this.location = event.location;
    this.description = event.description;
    this.participants = event.participants;

    this.commentService.getAllByEventId(event.id).subscribe((comments: List<Comment>) => {
      this.comments = comments;
    });
    this.mediaService.getAllByEventId(event.id).subscribe((mediaItems: List<MediaItem>) => {
      this.mediaItems = mediaItems;
    });
  }


  onCommentCreated(comment: Comment) {
    this.comments.add(comment);
  }


  deleteComment(comment: Comment) {
    this.commentService.delete(comment).subscribe({
      next: () => {
        this.comments.remove(comment);
      },
      error: (err) => {
        console.error('Failed to delete comment!', err);
      }
    });
  }


  editComment(comment: Comment) {
    this.commentService.edit(comment).subscribe({
      next: () => {
        console.log('edit comment:', comment);
      },
      error: (err) => {
        console.error('Failed to edit comment!', err);
      }
    });
  }

  uploadMedia(file: File) {
    this.mediaService.upload(this.id, file).subscribe({
      next: (mediaItem) => this.mediaItems.add(mediaItem),
      error: (err) => console.error('Failed to upload media!', err)
    });
  }

  deleteMedia(item: MediaItem) {
    this.mediaService.delete(this.id, item).subscribe({
      next: (mediaItem) => this.mediaItems.remove(mediaItem),
      error: (err) => console.error('Failed to delete media!', err)
    });
  }


}
