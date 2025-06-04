package at.msm.asobo.dto;

import at.msm.asobo.entities.User;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserAdminDTO {

    private UUID id;
    private String email;
    private String username;
    private List<EventDTO> createdEvents;
    private List<EventDTO> attendedEvents;
    private List<UserCommentDTO> comments;
    private URI pictureURI;
    private String location;
    private LocalDateTime registerDate;
    private boolean isActive;

    public UserAdminDTO() {
    }

    public UserAdminDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.createdEvents = user.getCreatedEvents().stream().map(EventDTO::new).toList();
        this.attendedEvents = user.getAttendedEvents().stream().map(EventDTO::new).toList();
        this.comments = user.getComments().stream().map(UserCommentDTO::new).toList();
        this.pictureURI = user.getPictureURI();
        this.location = user.getLocation();
        this.registerDate = user.getRegisterDate();
        this.isActive = user.isActive();
    }

    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public List<EventDTO> getCreatedEvents() {
        return this.createdEvents;
    }

    public List<EventDTO> getAttendedEvents() {
        return this.attendedEvents;
    }

    public List<UserCommentDTO> getComments() {
        return this.comments;
    }

    public URI getPictureURI() {
        return this.pictureURI;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getRegisterDate() {
        return this.registerDate;
    }

    public boolean getIsActive() {
        return this.isActive;
    }
}
