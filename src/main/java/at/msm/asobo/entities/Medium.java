package at.msm.asobo.entities;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;


@Entity
public class Medium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "URI is mandatory for media")
    protected URI mediumURI;

    @ManyToOne
    @JoinColumn(name = "event_id")
    // @JsonBackReference
    private Event event;

    public Medium() {
    }

    public Medium(MediumDTO mediumDTO) {
        this.mediumURI = mediumDTO.getMediumURI();
    }

    public Medium(MediumCreationDTO creationDTO) {
        this.mediumURI = creationDTO.getMediumURI();
        // this.event = new Event(creationDTO.getEvent());
    }


    public Medium(URI mediumURI) {
        this.mediumURI = mediumURI;
    }

    public URI getMediumURI() {
        return mediumURI;
    }

    public void setMediumURI(URI mediumURI) {
        this.mediumURI = mediumURI;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
