package at.msm.asobo.dto.event;

import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.user.EventCreatorDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EventCreationDTO {
    private UUID id;

    @NotBlank(message = "Title is mandatory for event creation")
    private String title;

    @NotBlank(message = "Description is mandatory for event creation")
    private String description;

    @NotBlank(message = "Location is mandatory for event creation")
    private String location;

    private boolean isPrivate;

    @NotNull(message = "Date is mandatory for event creation")
    @FutureOrPresent(message = "Date of event must be today or in the future")
    private LocalDateTime date;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    // add this again as soon as we have logged-in users
    // @NotNull(message = "Event creator is mandatory for event creation")
    // TODO change this to EventCreatorDTO when we have factory instead of mapper ticket #37
    // private EventCreatorDTO creator;
    private UserPublicDTO creator;

    private Set<UserPublicDTO> eventAdmins;

    private List<UserDTO> participants;

    private List<UserCommentDTO> comments;

    private List<MediumDTO> media;

    public EventCreationDTO() {}

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

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public LocalDateTime getModificationDate() {
        return this.modificationDate;
    }

    public UserPublicDTO getCreator() {
        return this.creator;
    }

    public List<UserDTO> getParticipants() {
        return this.participants;
    }

    public List<UserCommentDTO> getComments() {
        return this.comments;
    }

    public List<MediumDTO> getMedia() {
        return this.media;
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

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
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

    public Set<UserPublicDTO> getEventAdmins() {
        return this.eventAdmins;
    }

    public void setEventAdmins(Set<UserPublicDTO> eventAdmins) {
        this.eventAdmins = eventAdmins;
    }
}
