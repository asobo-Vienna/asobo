package at.msm.asobo.dto.filter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class EventFilterDTO {
  private String location;
  private UUID creatorId;
  private LocalDateTime date;
  private LocalDateTime dateFrom;
  private LocalDateTime dateTo;
  private Boolean isPrivateEvent;
  private Set<UUID> eventAdminIds;
  private Set<UUID> participantIds;

  public EventFilterDTO(
      String location,
      UUID creatorId,
      LocalDateTime date,
      LocalDateTime dateFrom,
      LocalDateTime dateTo,
      Boolean isPrivateEvent,
      Set<UUID> eventAdminsIds,
      Set<UUID> participantIds) {
    this.location = location;
    this.creatorId = creatorId;
    this.date = date;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.isPrivateEvent = isPrivateEvent;
    this.eventAdminIds = eventAdminsIds;
    this.participantIds = participantIds;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public LocalDateTime getDateFrom() {
    return this.dateFrom;
  }

  public void setDateFrom(LocalDateTime dateFrom) {
    this.dateFrom = dateFrom;
  }

  public LocalDateTime getDateTo() {
    return this.dateTo;
  }

  public void setDateTo(LocalDateTime dateTo) {
    this.dateTo = dateTo;
  }

  public UUID getCreatorId() {
    return this.creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public Boolean getIsPrivateEvent() {
    return this.isPrivateEvent;
  }

  public void setIsPrivateEvent(Boolean isPrivateEvent) {
    this.isPrivateEvent = isPrivateEvent;
  }

  public Set<UUID> getEventAdminIds() {
    return this.eventAdminIds;
  }

  public void setEventAdminIds(Set<UUID> eventAdminIds) {
    this.eventAdminIds = eventAdminIds;
  }

  public Set<UUID> getParticipantIds() {
    return this.participantIds;
  }

  public void setParticipantIds(Set<UUID> participantIds) {
    this.participantIds = participantIds;
  }
}
