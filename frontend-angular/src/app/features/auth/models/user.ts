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
  isDeleted: boolean;
  pictureURI: string;
  location: string;
  country: string;
  salutation: string;
  roles?: Role[];
}
