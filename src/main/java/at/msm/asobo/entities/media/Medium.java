package at.msm.asobo.entities.media;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.net.URI;
import java.util.UUID;

@Entity
public abstract class Medium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    protected URI mediumURI;

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
}
