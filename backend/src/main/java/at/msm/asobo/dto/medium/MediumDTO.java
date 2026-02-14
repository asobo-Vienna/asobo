package at.msm.asobo.dto.medium;

import at.msm.asobo.dto.user.UserPublicDTO;
import java.time.Instant;
import java.util.UUID;

public class MediumDTO {
  private UUID id;
  private UUID eventId;
  private String mediumURI;
  private UserPublicDTO creator;
  private Instant creationDate;

  public MediumDTO() {}

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getEventId() {
    return this.eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public void setMediumURI(String mediumURI) {
    this.mediumURI = mediumURI;
  }

  public UUID getId() {
    return this.id;
  }

  public String getMediumURI() {
    return this.mediumURI;
  }

  public UserPublicDTO getCreator() {
    return this.creator;
  }

  public void setCreator(UserPublicDTO creator) {
    this.creator = creator;
  }

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }
}
