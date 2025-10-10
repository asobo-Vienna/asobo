export interface Comment {
  id: string
  text: string;
  authorId: string;
  username: string;
  eventId: string;
  creationDate: Date;
  pictureURI: string;
}
// the ? are here so we can post new comments by only specifying authorId and text (backend gets the entire user by id)
