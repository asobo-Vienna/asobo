export interface EventSearchResult {
  id: string;
  title: string;
  description?: string;
  date?: string;
  location?: string;
  pictureURI?: string;
  creatorName?: string;
  participantCount?: number;
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

// Type used for autocomplete items
export type AutocompleteItem =
  (EventSearchResult & { name: string; pictureURI: string, additionalInfo: string, location: string, type: 'EVENT' })
  | (UserSearchResult & { name: string; pictureURI: string, additionalInfo: string, location: string, type: 'USER' });
