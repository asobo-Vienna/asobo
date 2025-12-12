package at.msm.asobo.interfaces;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserCommentWithEventTitle {
    UUID getId();
    String getText();
    String getUsername();
    UUID getAuthorId();
    UUID getEventId();
    String getPictureURI();
    LocalDateTime getCreationDate();
    LocalDateTime getModificationDate();
    String getEventTitle();
}
