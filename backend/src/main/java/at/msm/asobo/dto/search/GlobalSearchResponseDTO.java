package at.msm.asobo.dto.search;

import java.util.List;

public class GlobalSearchResponseDTO {
  private List<EventSearchResultDTO> events;
  private List<UserSearchResultDTO> users;
  private int totalResults;

  public GlobalSearchResponseDTO() {}

  public List<EventSearchResultDTO> getEvents() {
    return this.events;
  }

  public void setEvents(List<EventSearchResultDTO> events) {
    this.events = events;
  }

  public List<UserSearchResultDTO> getUsers() {
    return this.users;
  }

  public void setUsers(List<UserSearchResultDTO> users) {
    this.users = users;
  }

  public int getTotalResults() {
    return this.totalResults;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }
}
