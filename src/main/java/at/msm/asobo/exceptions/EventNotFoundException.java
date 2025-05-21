package at.msm.asobo.exceptions;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(UUID id) {
        super("Event with ID " + id + " not found.");
    }
}
