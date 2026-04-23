package at.msm.asobo.dto.event;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.EventCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class EventBaseDTO {
  private UUID id;
  private String title;
  private String description;
  private String location;
  private LocalDateTime date;
  private Instant creationDate;
  private Instant modificationDate;
  private Set<EventCategory> categories;

  @JsonProperty("isPrivateEvent")
  private boolean isPrivateEvent;

  // TODO refactor to a new DTO EventCreator (after refactoring mappers to factories)
  // private EventCreatorDTO creator;
  private UserPublicDTO creator;
  private String pictureURI;

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

  public Set<EventCategory> getCategories() {
    return this.categories;
  }

  public void setCategories(Set<EventCategory> categories) {
    this.categories = categories;
  }

  public boolean getIsPrivateEvent() {
    return this.isPrivateEvent;
  }

  public void setIsPrivateEvent(boolean isPrivateEvent) {
    this.isPrivateEvent = isPrivateEvent;
  }

  public UserPublicDTO getCreator() {
    return this.creator;
  }

  public void setCreator(UserPublicDTO creator) {
    this.creator = creator;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }
}
