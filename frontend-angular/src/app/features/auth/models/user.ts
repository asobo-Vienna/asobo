import {Role} from '../../../shared/enums/Role';

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
  roles?: Set<Role>;
}
