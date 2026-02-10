package at.msm.asobo.dto.comment;

public class UserCommentWithEventTitleDTO extends UserCommentDTO {
  private String eventTitle;

  public UserCommentWithEventTitleDTO() {}

  public String getEventTitle() {
    return this.eventTitle;
  }

  public void setEventTitle(String eventTitle) {
    this.eventTitle = eventTitle;
  }
}
