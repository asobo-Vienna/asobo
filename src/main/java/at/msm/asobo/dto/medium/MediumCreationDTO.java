package at.msm.asobo.dto.medium;

import at.msm.asobo.entities.Medium;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.UUID;

public class MediumCreationDTO {

    @NotNull(message = "Event ID is mandatory for creating a new medium")
    protected UUID eventId;

    @NotNull(message = "URI is mandatory for creating a new medium")
    protected URI mediumURI;

    public MediumCreationDTO() {
    }

    public MediumCreationDTO(Medium medium) {
        this.eventId = medium.getEvent().getId();
        this.mediumURI = medium.getMediumURI();
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
