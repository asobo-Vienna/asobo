package at.msm.asobo.dto.search;

import at.msm.asobo.entities.EventCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EventSearchResultDTO {
  private UUID id;
  private String title;
  private String description;
  private LocalDateTime date;
  private String location;
  private String pictureURI;
  private String creatorName;
  private UUID creatorId;
  private List<UUID> eventAdminIds;
  private int participantCount;
  private boolean isPrivateEvent;
  private String type = "EVENT";
  private Set<EventCategory> categories;

  public EventSearchResultDTO() {}

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

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Set<EventCategory> getCategories() {
    return this.categories;
  }

  public void setCategories(Set<EventCategory> categories) {
    this.categories = categories;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public UUID getCreatorId() {
    return this.creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public List<UUID> getEventAdminIds() {
    return this.eventAdminIds;
  }

  public void setEventAdminIds(List<UUID> eventAdminIds) {
    this.eventAdminIds = eventAdminIds;
  }

  public int getParticipantCount() {
    return this.participantCount;
  }

  public void setParticipantCount(int participantCount) {
    this.participantCount = participantCount;
  }

  public boolean isPrivateEvent() {
    return this.isPrivateEvent;
  }

  public void setPrivateEvent(boolean privateEvent) {
    this.isPrivateEvent = privateEvent;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
