export interface Comment {
  id?: string
  text: string;
  authorId?: string;
  username?: string;
  eventId: string;
  creationDate?: Date;
  pictureURI?: string;
}
