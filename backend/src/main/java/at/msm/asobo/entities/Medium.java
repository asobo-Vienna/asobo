package at.msm.asobo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


@Entity
public class Medium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "URI is mandatory for media")
    @Column(length = 4096)
    private String mediumURI;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Medium() {
    }

    public String getMediumURI() {
        return this.mediumURI;
    }

    public void setMediumURI(String mediumURI) {
        this.mediumURI = mediumURI;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
