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

@Component({
  selector: 'app-event-detail-page',
  imports: [
    DatePipe,
    NewComment,
    Participants,
    CommentsList,
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
  comments: Comment[] = [];
  participants: Participant[] = [];

  constructor(private route: ActivatedRoute,
              private eventService: EventService,
              private commentService: CommentService) {
  }

  // TODO don't double fetch participants & comments find a good solution
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
    console.log(this.eventService.getEventById(eventId));
    return this.eventService.getEventById(eventId);
  }

  private populateEvent(event: Event): void {
    this.title = event.title;
    this.pictureURI = event.pictureURI;
    this.date = event.date;
    this.time = event.date;
    this.location = event.location;
    this.description = event.description;
    this.participants = event.participants;
    this.commentService.getAllByEventId(event.id).subscribe(comments => {
      this.comments = comments;
    });
  }

  onCommentCreated(comment: Comment) {
    this.comments.push(comment);
  }

  deleteComment(commentToDelete: Comment) {
    this.commentService.delete(commentToDelete).subscribe({
      next: () => {
        this.comments = this.comments.filter(c => c.id !== commentToDelete.id);
      },
      error: (err) => {
        console.error('Failed to delete comment!', err);
      }
    });
  }

  editComment(commentToEdit: Comment) {
    this.commentService.edit(commentToEdit).subscribe({
      next: () => {
        console.log('edit comment:', commentToEdit);
      },
      error: (err) => {
        console.error('Failed to edit comment!:', err);
      }
    });
  }
}
