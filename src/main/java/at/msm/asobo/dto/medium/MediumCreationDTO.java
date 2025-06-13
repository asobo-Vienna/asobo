package at.msm.asobo.dto.medium;

import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.UUID;

public class MediumCreationDTO {

    protected UUID eventId;

    @NotNull(message = "URI is mandatory for creating a new medium")
    protected URI mediumURI;

    public MediumCreationDTO() {
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setMediumURI(URI mediumURI) {
        this.mediumURI = mediumURI;
    }

    public URI getMediumURI() {
        return this.mediumURI;
    }
}
