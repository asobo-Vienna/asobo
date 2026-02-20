import {EventSummary} from '../../features/events/models/event-summary';

export interface EventSearchResult {
  id: string;
  title: string;
  description?: string;
  date?: string;
  location?: string;
  pictureURI?: string;
  creatorName?: string;
  participantCount?: number;
  commentCount?: number;
  mediaCount?: number;
  eventAdminCount?: number;
  isPrivateEvent?: boolean;
  type: 'EVENT';
}

export interface UserSearchResult {
  id: string;
  username?: string;
  firstName?: string;
  surname?: string;
  fullName?: string;
  aboutMe?: string;
  pictureURI?: string;
  location?: string;
  createdEventsCount?: number;
  type: 'USER';
}

export interface GlobalSearchResponse {
  events: EventSearchResult[];
  users: UserSearchResult[];
  totalResults: number;
}

export interface UserSearchResultBasic {
  id: string;
  username: string;
  pictureURI: string;
  fullName: string;
  location: string;
}

export interface SearchResultsResponse {
  events: EventSummary[];
  users: {
    id: string;
    username: string;
    pictureURI: string;
    fullName: string;
    location: string;
  }[];
  totalResults: number;
}

// Type used for autocomplete items
export type AutocompleteItem =
  (EventSearchResult & { name: string; pictureURI: string, additionalInfo: string, location: string, type: 'EVENT' })
  | (UserSearchResult & { name: string; pictureURI: string, additionalInfo: string, location: string, type: 'USER' });
