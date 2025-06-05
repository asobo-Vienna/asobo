package at.msm.asobo.entities;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User is mandatory for creating event")
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(name = "participant_id")
    private List<User> participants;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Date must be specified")
    private LocalDateTime date;

    @NotBlank(message = "Location is mandatory")
    private String location;

    private URI pictureURI;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @OneToMany(mappedBy = "event")
    private List<UserComment> comments;

    @OneToMany(mappedBy = "event")
    private List<Medium> media;

    public Event() {
    }


    public Event(EventCreationDTO eventCreationDTO) {
        this.title = eventCreationDTO.getTitle();
        this.description = eventCreationDTO.getTitle();
        this.date = eventCreationDTO.getDate();
        this.location = eventCreationDTO.getLocation();
        this.creator = new User();
        this.creator.setUsername(eventCreationDTO.getCreator().getUsername());
        this.creator.setPictureURI(eventCreationDTO.getCreator().getPictureURI());

        this.participants = Optional.ofNullable(eventCreationDTO.getParticipants())
                .orElse(List.of())
                .stream()
                .map(User::new)
                .toList();

        this.creationDate = LocalDateTime.now();
        this.modificationDate = null;

        this.comments = Optional.ofNullable(eventCreationDTO.getComments())
                .orElse(List.of())
                .stream()
                .map(UserComment::new)
                .toList();

        this.media = Optional.ofNullable(eventCreationDTO.getMedia())
                .orElse(List.of())
                .stream()
                .map(Medium::new)
                .toList();

        this.pictureURI = eventCreationDTO.getPictureURI();

    }

    public Event(EventDTO eventDTO){
        this.title = eventDTO.getTitle();
        this.description = eventDTO.getTitle();
        this.date = eventDTO.getDate();
        this.location = eventDTO.getLocation();

        this.participants = Optional.ofNullable(eventDTO.getParticipants())
                .orElse(List.of())
                .stream()
                .map(User::new)
                .toList();

        this.creationDate = eventDTO.getCreationDate();
        this.modificationDate = eventDTO.getModificationDate();

        this.comments = Optional.ofNullable(eventDTO.getComments())
                .orElse(List.of())
                .stream()
                .map(UserComment::new)
                .toList();

        this.media = Optional.ofNullable(eventDTO.getMedia())
                .orElse(List.of())
                .stream()
                .map(Medium::new)
                .toList();

        this.pictureURI = eventDTO.getPictureURI();
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URI getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<UserComment> getComments() {
        return comments;
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
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
