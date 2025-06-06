package at.msm.asobo.dto.medium;

import at.msm.asobo.entities.Medium;
import java.net.URI;
import java.util.UUID;

public class MediumDTO {

    private UUID id;
    private UUID eventId;
    protected URI mediumURI;


    public MediumDTO() {
    }

    public MediumDTO(Medium medium) {
        this.id = medium.getId();
        this.eventId = medium.getEvent().getId();
        this.mediumURI = medium.getMediumURI();
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getId() {
        return this.id;
    }

    public URI getMediumURI() {
        return this.mediumURI;
    }
}


