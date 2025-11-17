// environment.ts (development)
export const BACKEND_URL = 'http://localhost:8080';
export const FRONTEND_URL = 'http://localhost:4200';
export const API_BASE_URL = `${BACKEND_URL}/api`;
export const JWT_TOKEN_STORAGE_KEY = 'jwt_token';
export const USER_STORAGE_KEY = 'current_user';

export const environment = {
  production: false,
  backendUrl: BACKEND_URL,
  apiBaseUrl: API_BASE_URL,
  eventsEndpoint: `${API_BASE_URL}/events`,
  loginEndpoint: `${API_BASE_URL}/auth/login`,
  registerEndpoint: `${API_BASE_URL}/auth/register`,
  usersEndpoint: `${API_BASE_URL}/users`,
  frontendBaseUrl: FRONTEND_URL,
  JWT_TOKEN_STORAGE_KEY: JWT_TOKEN_STORAGE_KEY,
  USER_STORAGE_KEY: USER_STORAGE_KEY,
  defaultSalutations: ['Mr.', 'Ms.', 'Other'],
  minIdentifierLength: 3,
  minPWLength: 6,
  maxAboutMeLength: 1000,
  minEventTitleLength: 2,
  maxEventTitleLength: 100,
  minEventDescriptionLength: 3,
  maxEventDescriptionLength: 1000,
  specialCharactersForPW: /[!@#$%^&*(),.?":;{}|<>]/,
  userProfileBaseUrl: '/user/',
  adminSectionBaseUrl: '/admin',
  userDummyProfilePicRelativeUrl: '/uploads/profile-pictures/default.png',
  defaultDebounceTimeForFormFields: 500,
};
