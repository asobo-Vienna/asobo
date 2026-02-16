package at.msm.asobo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserComment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank(message = "Text is mandatory")
  private String text;

  @NotNull(message = "Author is required to create a user comment")
  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @CreatedDate private Instant creationDate;

  @LastModifiedDate private Instant modificationDate;

  // private File file;

  public UserComment() {}

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
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

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return this.id;
  }

  public Event getEvent() {
    return this.event;
  }

  public User getAuthor() {
    return this.author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserComment comment = (UserComment) o;
    return Objects.equals(id, comment.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
