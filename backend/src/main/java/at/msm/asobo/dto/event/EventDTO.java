package at.msm.asobo.dto.event;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class EventDTO {

  private UUID id;

  private String title;

  private String description;

  private String pictureURI;

  private String location;

  private LocalDateTime date;

  private Instant creationDate;

  private Instant modificationDate;

  // TODO refactor to a new DTO EventCreator (after refactoring mappers to factories) private
  // EventCreatorDTO creator;
  private UserPublicDTO creator;

  private Set<UserPublicDTO> eventAdmins;

  private boolean isPrivate;

  private Set<UserPublicDTO> participants;

  private List<UserCommentDTO> comments;

  private List<MediumDTO> media;

  public EventDTO() {
    this.participants = new HashSet<>();
    this.comments = new ArrayList<>();
    this.media = new ArrayList<>();
    this.eventAdmins = new HashSet<>();
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

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
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

  public void setParticipants(Set<UserPublicDTO> participants) {
    this.participants = participants;
  }

  public void setComments(List<UserCommentDTO> comments) {
    this.comments = comments;
  }

  public void setMedia(List<MediumDTO> media) {
    this.media = media;
  }

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

  public LocalDateTime getDate() {
    return this.date;
  }

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public UserPublicDTO getCreator() {
    return this.creator;
  }

  public boolean getIsPrivate() {
    return this.isPrivate;
  }

  public void setIsPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public Set<UserPublicDTO> getParticipants() {
    return this.participants;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public List<UserCommentDTO> getComments() {
    return this.comments;
  }

  public List<MediumDTO> getMedia() {
    return this.media;
  }

  public Set<UserPublicDTO> getEventAdmins() {
    return eventAdmins;
  }

  public void setEventAdmins(Set<UserPublicDTO> eventAdmins) {
    this.eventAdmins = eventAdmins;
  }
}
