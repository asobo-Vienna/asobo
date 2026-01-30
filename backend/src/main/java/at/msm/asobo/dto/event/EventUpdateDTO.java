package at.msm.asobo.dto.event;

import at.msm.asobo.dto.user.UserPublicDTO;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventUpdateDTO {

    private String title;

    private String description;

    private MultipartFile picture;

    private String location;

    private boolean isPrivate;

    private LocalDateTime date;

    private Set<UserPublicDTO> participants;

    public EventUpdateDTO() {
        this.participants = new HashSet<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Set<UserPublicDTO> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<UserPublicDTO> participants) {
        this.participants = participants;
    }

    public MultipartFile getPicture() {
        return this.picture;
    }
}
