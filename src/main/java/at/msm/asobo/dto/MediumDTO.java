package at.msm.asobo.dto;

import at.msm.asobo.entities.Medium;
import java.net.URI;
import java.util.UUID;

public class MediumDTO {

    private UUID id;

    protected URI mediumURI;

    private EventDTO event;


    public MediumDTO() {
    }

    public MediumDTO(Medium medium) {
        this.id = medium.getId();
        this.mediumURI = medium.getMediumURI();
        this.event = new EventDTO(medium.getEvent());
    }

    public UUID getId() {
        return this.id;
    }

    public URI getMediumURI() {
        return this.mediumURI;
    }

    public EventDTO getEvent() {
        return this.event;
    }
}
