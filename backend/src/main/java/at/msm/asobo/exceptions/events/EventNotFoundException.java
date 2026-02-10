package at.msm.asobo.exceptions.events;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(UUID id) {
        super("Could not find event with ID " + id);
    }

    public EventNotFoundException(String message) {
        super(message);
    }
}
