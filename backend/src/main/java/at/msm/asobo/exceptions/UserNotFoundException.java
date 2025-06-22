package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Could not find user with ID " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}