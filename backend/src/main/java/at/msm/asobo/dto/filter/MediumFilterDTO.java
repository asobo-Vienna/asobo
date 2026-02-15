package at.msm.asobo.dto.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public class MediumFilterDTO {
  private UUID creatorId;
  private UUID eventId;
  private LocalDateTime date;
  private LocalDateTime dateFrom;
  private LocalDateTime dateTo;

  public MediumFilterDTO(
      UUID authorId,
      UUID eventId,
      LocalDateTime date,
      LocalDateTime dateFrom,
      LocalDateTime dateTo) {
    this.creatorId = authorId;
    this.eventId = eventId;
    this.date = date;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  public UUID getCreatorId() {
    return this.creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public UUID getEventId() {
    return this.eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
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
}
