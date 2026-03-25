package at.msm.asobo.exceptions.events;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventInThePastException extends RuntimeException {
    public EventInThePastException(String message) {
        super(message);
    }
}
