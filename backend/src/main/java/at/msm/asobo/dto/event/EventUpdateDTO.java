package at.msm.asobo.dto.event;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventUpdateDTO {

    private UUID id;

    private String title;

    private String description;

    private MultipartFile picture;

    private String location;

    private LocalDateTime date;

    private List<UserPublicDTO> participants;

    private List<UserCommentDTO> comments;

    private List<MediumDTO> media;

    public EventUpdateDTO() {
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

    public List<UserPublicDTO> getParticipants() {
        return participants;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public List<UserCommentDTO> getComments() {
        return comments;
    }

    public List<MediumDTO> getMedia() {
        return media;
    }
}
