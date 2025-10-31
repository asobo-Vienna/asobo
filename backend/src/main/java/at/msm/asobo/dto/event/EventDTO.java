package at.msm.asobo.dto.event;

import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.user.EventCreatorDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventDTO {

    private UUID id;

    private String title;

    private String description;

    private String pictureURI;

    private String location;

    private LocalDateTime date;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    // TODO refactor to a new DTO EventCreator (after refactoring mappers to factories) private EventCreatorDTO creator;
    private UserPublicDTO creator;

    private List<UserPublicDTO> participants;

    private List<UserCommentDTO> comments;

    private List<MediumDTO> media;

    public EventDTO() {
        this.participants = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.media = new ArrayList<>();
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

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setCreator(UserPublicDTO creator) {
        this.creator = creator;
    }

    public void setParticipants(List<UserPublicDTO> participants) {
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

    public UserPublicDTO getCreator() {
        return creator;
    }

    public List<UserPublicDTO> getParticipants() {
        return participants;
    }

    public String getPictureURI() {
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
