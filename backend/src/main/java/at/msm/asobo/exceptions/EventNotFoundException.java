package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(UUID id) {
        super("Could not find event with ID " + id);
    }

    public EventNotFoundException(String message){
        super(message);
    }
}
