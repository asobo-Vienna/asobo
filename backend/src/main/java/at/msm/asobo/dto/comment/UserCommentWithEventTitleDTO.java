package at.msm.asobo.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCommentWithEventTitleDTO {

    @JsonProperty("userComment")
    private UserCommentDTO userCommentDTO;

    private String eventTitle;

    public UserCommentWithEventTitleDTO() {
    }

    public UserCommentDTO getUserCommentDTO() {
        return this.userCommentDTO;
    }

    public void setUserCommentDTO(UserCommentDTO userCommentDTO) {
        this.userCommentDTO = userCommentDTO;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
