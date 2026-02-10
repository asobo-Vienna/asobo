package at.msm.asobo.exceptions.users;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(UUID id) {
    super("Could not find user with ID " + id);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
