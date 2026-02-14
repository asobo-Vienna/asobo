package at.msm.asobo.dto.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserCommentFilterDTO {
  private UUID authorId;
  private UUID eventId;
  private LocalDateTime dateFrom;
  private LocalDateTime dateTo;

  public UserCommentFilterDTO(
      UUID authorId, UUID eventId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    this.authorId = authorId;
    this.eventId = eventId;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  public void setAuthorId(UUID authorId) {
    this.authorId = authorId;
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
