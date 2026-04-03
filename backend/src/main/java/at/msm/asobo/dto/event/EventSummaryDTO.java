package at.msm.asobo.dto.event;

import java.util.Set;
import java.util.UUID;

public class EventSummaryDTO extends EventBaseDTO {

  // Counts instead of full lists
  private int participantCount;
  private int commentCount;
  private int mediaCount;
  private int eventAdminCount;
  private Set<UUID> eventAdminIds;

  public EventSummaryDTO() {}

  public int getParticipantCount() {
    return this.participantCount;
  }

  public void setParticipantCount(int participantCount) {
    this.participantCount = participantCount;
  }

  public int getCommentCount() {
    return this.commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public int getMediaCount() {
    return this.mediaCount;
  }

  public void setMediaCount(int mediaCount) {
    this.mediaCount = mediaCount;
  }

  public int getEventAdminCount() {
    return this.eventAdminCount;
  }

  public void setEventAdminCount(int eventAdminCount) {
    this.eventAdminCount = eventAdminCount;
  }

  public Set<UUID> getEventAdminIds() {
    return this.eventAdminIds;
  }

  public void setEventAdminIds(Set<UUID> eventAdminIds) {
    this.eventAdminIds = eventAdminIds;
  }
}
