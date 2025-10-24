export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  surname: string;
  registerDate: Date;
  isActive: boolean;
  pictureURI: string;
  location: string;
  salutation: string;
}
