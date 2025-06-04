package at.msm.asobo.dto;

import at.msm.asobo.entities.UserComment;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserCommentDTO {

    private UUID id;
    private String text;
    private UserDTO author;
    private EventDTO event;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    // private File file;

    public UserCommentDTO() {
    }

    public UserCommentDTO(UserComment userComment) {
        this.id = userComment.getId();
        this.text = userComment.getText();
        this.author = new UserDTO(userComment.getAuthor());
        this.event = new EventDTO(userComment.getEvent());
        this.creationDate = userComment.getCreationDate();
        this.modificationDate = userComment.getModificationDate();
    }

    public UUID getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public UserDTO getAuthor() {
        return this.author;
    }

    public EventDTO getEvent() {
        return this.event;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public LocalDateTime getModificationDate() {
        return this.modificationDate;
    }
}
