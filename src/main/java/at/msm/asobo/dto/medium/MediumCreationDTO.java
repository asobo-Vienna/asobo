package at.msm.asobo.dto.medium;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.entities.Medium;
import java.net.URI;

public class MediumCreationDTO {

    protected URI mediumURI;

    public MediumCreationDTO() {
    }

    public MediumCreationDTO(Medium medium) {
        this.mediumURI = medium.getMediumURI();
    }

    public URI getMediumURI() {
        return this.mediumURI;
    }


}
