package at.msm.asobo.dto.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public class MediumFilterDTO {
  private UUID creatorId;
  private UUID eventId;
  private LocalDateTime dateFrom;
  private LocalDateTime dateTo;

  public MediumFilterDTO(
      UUID authorId, UUID eventId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    this.creatorId = authorId;
    this.eventId = eventId;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  public UUID getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public LocalDateTime getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(LocalDateTime dateFrom) {
    this.dateFrom = dateFrom;
  }

  public LocalDateTime getDateTo() {
    return dateTo;
  }

  public void setDateTo(LocalDateTime dateTo) {
    this.dateTo = dateTo;
  }
}
