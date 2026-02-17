package at.msm.asobo.dto.search;

import java.time.LocalDateTime;

public class GlobalSearchRequestDTO {
  private String query;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String location;
  private Boolean includePrivateEvents = false;
  private Boolean includeUsers = false;

  public GlobalSearchRequestDTO() {}

  public String getQuery() {
    return this.query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public LocalDateTime getStartDate() {
    return this.startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return this.endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Boolean getIncludePrivateEvents() {
    return this.includePrivateEvents;
  }

  public void setIncludePrivateEvents(Boolean includePrivateEvents) {
    this.includePrivateEvents = includePrivateEvents;
  }

  public Boolean getIncludeUsers() {
    return this.includeUsers;
  }

  public void setIncludeUsers(Boolean includeUsers) {
    this.includeUsers = includeUsers;
  }
}
