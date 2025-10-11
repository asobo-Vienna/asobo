package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyJoinedException extends RuntimeException {

    public UserAlreadyJoinedException() {
        super("You have already joined this event!");
    }

    public UserAlreadyJoinedException(String message) {
        super(message);
    }
}
