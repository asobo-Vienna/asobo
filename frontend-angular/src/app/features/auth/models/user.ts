import {Role} from '../../../shared/entities/role';

export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  surname: string;
  aboutMe: string;
  registerDate: Date;
  isActive: boolean;
  pictureURI: string;
  location: string;
  salutation: string;
  roles?: Role[];
}
