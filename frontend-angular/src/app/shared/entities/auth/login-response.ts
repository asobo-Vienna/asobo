import {User} from '../users/user';

export interface LoginResponse {
  token: string;
  user: User;
}
