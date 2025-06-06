package at.msm.asobo.dto.comment;

import at.msm.asobo.entities.UserComment;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserCommentDTO {

    private UUID id;
    private String text;
    private UUID authorId;
    private UUID eventId;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    // private File file;

    public UserCommentDTO() {
    }

    public UserCommentDTO(UserComment userComment) {
        this.id = userComment.getId();
        this.text = userComment.getText();
        this.authorId = userComment.getAuthor().getId();
        this.eventId = userComment.getEvent().getId();
        this.creationDate = userComment.getCreationDate();
        this.modificationDate = userComment.getModificationDate();
    }

    public UUID getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public LocalDateTime getModificationDate() {
        return this.modificationDate;
    }
}
