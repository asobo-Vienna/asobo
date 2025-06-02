package at.msm.asobo.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Entity
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

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    // private File file;

    public UserComment(){
    }

    public UserComment(String text, User user) {
        this.text = text;
        this.author = user;
        this.creationDate = LocalDateTime.now();
        this.modificationDate = null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setEvent(Event event) {
    }
}
