package at.msm.asobo.dto.event;

import at.msm.asobo.dto.user.UserPublicDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventSummaryDTO {
  private UUID id;
  private String title;
  private String description;
  private String pictureURI;
  private String location;
  private LocalDateTime date;
  private Instant creationDate;
  private Instant modificationDate;
  private UserPublicDTO creator;
  private boolean isPrivate;

  // Counts instead of full lists
  private int participantCount;
  private int commentCount;
  private int mediaCount;
  private int eventAdminCount;

  public EventSummaryDTO() {}

  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
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

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public UserPublicDTO getCreator() {
    return this.creator;
  }

  public void setCreator(UserPublicDTO creator) {
    this.creator = creator;
  }

  public boolean getIsPrivate() {
    return this.isPrivate;
  }

  public void setIsPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

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
}
