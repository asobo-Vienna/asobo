import {Comment} from './comment';

export interface CommentWithEventTitle {
  userComment: Comment;
  eventTitle: string;
}
