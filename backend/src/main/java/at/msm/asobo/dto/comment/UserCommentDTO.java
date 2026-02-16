package at.msm.asobo.dto.comment;

import java.time.Instant;
import java.util.UUID;

public class UserCommentDTO {

  private UUID id;
  private String username;
  private String text;
  private UUID authorId;
  private UUID eventId;
  private String pictureURI;
  private Instant creationDate;
  private Instant modificationDate;

  // private File file;

  public UserCommentDTO() {}

  public UUID getId() {
    return this.id;
  }

  public String getText() {
    return this.text;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setText(String text) {
    this.text = text;
  }

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  public void setAuthorId(UUID authorId) {
    this.authorId = authorId;
  }

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public String getPictureURI() {
    return pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
