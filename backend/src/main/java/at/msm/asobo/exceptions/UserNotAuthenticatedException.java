package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(UUID id) {
        super("This user is not authenticated.");
    }

    public UserNotAuthenticatedException(String message){
        super (message);
    }
}
