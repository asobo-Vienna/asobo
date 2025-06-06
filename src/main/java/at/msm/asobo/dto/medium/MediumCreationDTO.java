package at.msm.asobo.dto.medium;

import at.msm.asobo.entities.Medium;
import java.net.URI;
import java.util.UUID;

public class MediumCreationDTO {

    protected UUID eventId;
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
