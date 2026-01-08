package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserCommentNotFoundException extends RuntimeException {
    public UserCommentNotFoundException(UUID id) {
        super("Could not find user comment with ID " + id + ".");
    }

    public UserCommentNotFoundException(String message) {
        super(message);
    }
}
