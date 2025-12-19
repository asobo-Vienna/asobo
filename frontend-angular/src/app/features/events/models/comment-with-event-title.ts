import {Comment} from './comment';

export interface CommentWithEventTitle extends Comment {
  eventTitle: string;
}
