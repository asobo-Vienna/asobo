package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotAuthorizedException extends RuntimeException {

    public UserNotAuthorizedException() {
        super("This user is not authorized to perform this action.");
    }

    public UserNotAuthorizedException(String message){
        super (message);
    }
}
