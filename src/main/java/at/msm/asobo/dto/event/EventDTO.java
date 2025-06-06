package at.msm.asobo.dto.event;

import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.entities.Event;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private List<UserDTO> participants;

    private List<UserCommentDTO> comments;

    private List<MediumDTO> media;

    public EventDTO() {
        this.participants = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.media = new ArrayList<>();
    }

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.creator = new EventCreatorDTO(event.getCreator());
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.date = event.getDate();
        this.creationDate = event.getCreationDate();
        this.modificationDate = event.getModificationDate();

        this.media = Optional.ofNullable(event.getMedia())
                .orElse(List.of()) // or Collections.emptyList()
                .stream()
                .map(MediumDTO::new)
                .toList();

        this.comments = Optional.ofNullable(event.getComments())
                .orElse(List.of()) // or Collections.emptyList()
                .stream()
                .map(UserCommentDTO::new)
                .toList();

        this.pictureURI = event.getPictureURI();

        this.participants = Optional.ofNullable(event.getParticipants())
                .orElse(List.of())
                .stream()
                .map(UserDTO::new)
                .toList();
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

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setCreator(EventCreatorDTO creator) {
        this.creator = creator;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }

    public void setComments(List<UserCommentDTO> comments) {
        this.comments = comments;
    }

    public void setMedia(List<MediumDTO> media) {
        this.media = media;
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

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public URI getPictureURI() {
        return pictureURI;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public List<UserCommentDTO> getComments() {
        return comments;
    }

    public List<MediumDTO> getMedia() {
        return media;
    }
}
