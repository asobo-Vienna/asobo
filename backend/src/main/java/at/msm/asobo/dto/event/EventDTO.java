package at.msm.asobo.dto.event;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import java.util.*;

public class EventDTO extends EventBaseDTO {

  private Set<UserPublicDTO> participants;
  private List<UserCommentDTO> comments;
  private List<MediumDTO> media;
  private Set<UserPublicDTO> eventAdmins;

  public EventDTO() {
    this.participants = new HashSet<>();
    this.comments = new ArrayList<>();
    this.media = new ArrayList<>();
    this.eventAdmins = new HashSet<>();
  }

  public void setParticipants(Set<UserPublicDTO> participants) {
    this.participants = participants;
  }

  public void setComments(List<UserCommentDTO> comments) {
    this.comments = comments;
  }

  public void setMedia(List<MediumDTO> media) {
    this.media = media;
  }

  public Set<UserPublicDTO> getParticipants() {
    return this.participants;
  }

  public List<UserCommentDTO> getComments() {
    return this.comments;
  }

  public List<MediumDTO> getMedia() {
    return this.media;
  }

  public Set<UserPublicDTO> getEventAdmins() {
    return eventAdmins;
  }

  public void setEventAdmins(Set<UserPublicDTO> eventAdmins) {
    this.eventAdmins = eventAdmins;
  }
}
