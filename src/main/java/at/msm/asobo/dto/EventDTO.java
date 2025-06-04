package at.msm.asobo.dto;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.entities.Medium;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventDTO {

    private UUID id;

    private String title;

    private String description;

    private URI pictureURI;

    private String location;

    private LocalDateTime date;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    private EventCreatorDTO creator;

    // TODO save as user DTOs
    private List<User> participants;

    // TODO usercomment DTOs?
    private List<UserComment> comments;

    private List<Medium> media;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.creator = new EventCreatorDTO(event.getCreator());
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.date = event.getDate();
        this.creationDate = event.getCreationDate();
        this.modificationDate = event.getModificationDate();
        this.media = event.getMedia();
        this.comments = event.getComments();
        this.pictureURI = event.getPictureURI();
        this.participants = event.getParticipants();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public EventCreatorDTO getCreator() {
        return creator;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public URI getPictureURI() {
        return pictureURI;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public List<UserComment> getComments() {
        return comments;
    }

    public List<Medium> getMedia() {
        return media;
    }
}
