package at.msm.asobo.entities;

import at.msm.asobo.interfaces.PictureEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
      @Index(name = "idx_event_title", columnList = "title"),
      @Index(name = "idx_event_location", columnList = "location"),
      @Index(name = "idx_event_date", columnList = "date"),
      @Index(name = "idx_event_private", columnList = "is_private")
    })
public class Event implements PictureEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "User is mandatory for creating event")
  @ManyToOne
  @JoinColumn(name = "creator_id")
  private User creator;

  @ManyToMany private Set<User> eventAdmins;

  @ManyToMany private Set<User> participants;

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "Description is mandatory")
  @Column(length = 2000)
  private String description;

  @NotNull(message = "Date must be specified")
  private LocalDateTime date;

  @NotBlank(message = "Location is mandatory")
  private String location;

  @Column(length = 4096)
  private String pictureURI;

  @CreatedDate private Instant creationDate;

  @LastModifiedDate private Instant modificationDate;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserComment> comments;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Medium> media;

  @Column(name = "is_private", columnDefinition = "boolean default false")
  private boolean isPrivateEvent;

  public Event() {
    this.eventAdmins = new HashSet<>();
    this.participants = new HashSet<>();
    this.comments = new ArrayList<>();
    this.media = new ArrayList<>();
  }

  public User getCreator() {
    return this.creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public Set<User> getParticipants() {
    return this.participants;
  }

  public void setParticipants(Set<User> participants) {
    this.participants = participants;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String name) {
    this.title = name;
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

  @Override
  public String getPictureURI() {
    return this.pictureURI;
  }

  @Override
  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public Instant getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public List<UserComment> getComments() {
    return this.comments;
  }

  public void setComments(List<UserComment> comments) {
    this.comments = comments;
  }

  public List<Medium> getMedia() {
    return this.media;
  }

  public void setMedia(List<Medium> media) {
    this.media = media;
  }

  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public boolean isPrivateEvent() {
    return this.isPrivateEvent;
  }

  public void setPrivateEvent(boolean isPrivateEvent) {
    this.isPrivateEvent = isPrivateEvent;
  }

  public Set<User> getEventAdmins() {
    return this.eventAdmins;
  }

  public void setEventAdmins(Set<User> eventAdmins) {
    this.eventAdmins = eventAdmins;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Event event = (Event) o;
    return Objects.equals(id, event.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
