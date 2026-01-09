package at.msm.asobo.dto.event;

import at.msm.asobo.dto.user.UserPublicDTO;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventUpdateDTO {

    private String title;

    private String description;

    private MultipartFile picture;

    private String location;

    private LocalDateTime date;

    private boolean isPrivate;

    private List<UserPublicDTO> participants;

    public EventUpdateDTO() {
        this.participants = new ArrayList<>();
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

    public void setParticipants(List<UserPublicDTO> participants) {
        this.participants = participants;
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

    public List<UserPublicDTO> getParticipants() {
        return this.participants;
    }

    public MultipartFile getPicture() {
        return this.picture;
    }
}
