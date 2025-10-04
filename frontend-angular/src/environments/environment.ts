// environment.ts (development)
export const BACKEND_URL = 'http://localhost:8080';
export const FRONTEND_URL = 'http://localhost:4200';
export const API_BASE_URL = `${BACKEND_URL}/api`;

export const environment = {
  production: false,
  backendUrl: BACKEND_URL,
  apiBaseUrl: API_BASE_URL,
  eventsAddress: `${API_BASE_URL}/events`,
  loginEndpoint: `${API_BASE_URL}/auth/login`,
  frontendBaseUrl: FRONTEND_URL,
  minIdentifierLength: 3,
  minPWLength: 6,
  userProfileBaseUrl: `/user/`,
};
