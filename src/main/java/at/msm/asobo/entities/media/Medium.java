package at.msm.asobo.entities.media;

import at.msm.asobo.entities.Event;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.UUID;



@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = Picture.class, name = "picture"),
        @JsonSubTypes.Type(value = Video.class, name = "video")
})

@Entity
public abstract class Medium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "URI is mandatory for media")
    protected URI mediumURI;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Medium() {
    }

    public Medium(URI mediumURI){
        this.mediumURI=mediumURI;
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
