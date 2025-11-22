package at.msm.asobo.dto.user;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.event.EventDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserFullDTO {

    private UUID id;
    private String firstName;
    private String surname;
    private String email;
    private String username;
    private List<EventDTO> createdEvents;
    private List<EventDTO> attendedEvents;
    private List<UserCommentDTO> comments;
    private String pictureURI;
    private String location;
    private LocalDateTime registerDate;
    private boolean isActive;
    private String salutation;
    private String aboutMe;

    public UserFullDTO() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedEvents(List<EventDTO> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public void setAttendedEvents(List<EventDTO> attendedEvents) {
        this.attendedEvents = attendedEvents;
    }

    public void setComments(List<UserCommentDTO> comments) {
        this.comments = comments;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
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

    public String getPictureURI() {
        return this.pictureURI;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getRegisterDate() {
        return this.registerDate;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getAboutMe() {
        return this.aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}
