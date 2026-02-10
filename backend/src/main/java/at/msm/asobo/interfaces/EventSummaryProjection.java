package at.msm.asobo.interfaces;

import at.msm.asobo.entities.User;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventSummaryProjection {
  UUID getId();

  String getTitle();

  String getDescription();

  String getPictureURI();

  String getLocation();

  LocalDateTime getDate();

  LocalDateTime getCreationDate();

  LocalDateTime getModificationDate();

  User getCreator();

  boolean getIsPrivate();

  int getParticipantCount();

  int getCommentCount();

  int getMediaCount();
}
