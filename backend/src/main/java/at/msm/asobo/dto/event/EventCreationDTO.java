package at.msm.asobo.dto.event;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.enums.EventCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class EventCreationDTO {
  private UUID id;

  @NotBlank(message = "Title is mandatory for event creation")
  private String title;

  @NotBlank(message = "Description is mandatory for event creation")
  private String description;

  @NotBlank(message = "Location is mandatory for event creation")
  private String location;

  @NotNull(message = "Category is mandatory for event creation")
  private EventCategory category;

  @JsonProperty("isPrivateEvent")
  private boolean isPrivateEvent;

  @NotNull(message = "Date is mandatory for event creation")
  @FutureOrPresent(message = "Date of event must be today or in the future")
  private LocalDateTime date;

  private Instant creationDate;

  private Instant modificationDate;

  // TODO change this to EventCreatorDTO when we have factory instead of mapper ticket #37
  // private EventCreatorDTO creator;
  @NotNull(message = "Event creator is mandatory for event creation")
  private UserPublicDTO creator;

  private Set<UserPublicDTO> eventAdmins;

  public EventCreationDTO() {}

  public UUID getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public String getLocation() {
    return this.location;
  }

  public EventCategory getCategory() {
    return this.category;
  }

  public void setCategory(EventCategory category) {
    this.category = category;
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public UserPublicDTO getCreator() {
    return this.creator;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public void setCreator(UserPublicDTO creator) {
    this.creator = creator;
  }

  public boolean getIsPrivateEvent() {
    return this.isPrivateEvent;
  }

  public void setIsPrivateEvent(boolean isPrivateEvent) {
    this.isPrivateEvent = isPrivateEvent;
  }

  public Set<UserPublicDTO> getEventAdmins() {
    return this.eventAdmins;
  }

  public void setEventAdmins(Set<UserPublicDTO> eventAdmins) {
    this.eventAdmins = eventAdmins;
  }
}
