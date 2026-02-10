package at.msm.asobo.exceptions;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserCommentNotFoundException extends RuntimeException {
    public UserCommentNotFoundException(UUID id) {
        super("Could not find user comment with ID " + id + ".");
    }

    public UserCommentNotFoundException(String message) {
        super(message);
    }
}
