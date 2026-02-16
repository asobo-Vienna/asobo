package at.msm.asobo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Medium {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "URI is mandatory for media")
  @Column(length = 4096)
  private String mediumURI;

  @NotNull(message = "Creator is mandatory for creating media item")
  @ManyToOne
  @JoinColumn(name = "creator_id")
  private User creator;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @CreatedDate private Instant creationDate;

  @LastModifiedDate private Instant modificationDate;

  public Medium() {}

  public String getMediumURI() {
    return this.mediumURI;
  }

  public void setMediumURI(String mediumURI) {
    this.mediumURI = mediumURI;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return this.id;
  }

  public Event getEvent() {
    return this.event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public User getCreator() {
    return this.creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Medium medium = (Medium) o;
    return Objects.equals(id, medium.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
