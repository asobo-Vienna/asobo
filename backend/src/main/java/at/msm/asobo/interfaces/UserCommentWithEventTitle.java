package at.msm.asobo.interfaces;

import at.msm.asobo.entities.UserComment;

public interface UserCommentWithEventTitle {
    UserComment getComment();
    String getEventTitle();
}
