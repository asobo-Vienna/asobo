package at.msm.asobo.exceptions;

import javax.print.DocFlavor;
import java.util.UUID;

public class UserCommentNotFoundException extends RuntimeException {
    public UserCommentNotFoundException(String message) {
        super(message);
    }

    public UserCommentNotFoundException(UUID id) {
        super("Could not find user comment with ID " + id + ".");
    }
}
