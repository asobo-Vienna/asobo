package at.msm.asobo.exceptions.events;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventAdminException extends RuntimeException {
  public EventAdminException(String message) {
    super(message);
  }
}
