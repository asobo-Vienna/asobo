package at.msm.asobo.exceptions.users;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(UUID id) {
        super("This user is not authenticated.");
    }

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
