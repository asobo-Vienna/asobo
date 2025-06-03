package at.msm.asobo.entities.media;

import jakarta.persistence.Entity;

import java.net.URI;

@Entity
public class Picture extends Medium {

    public Picture(URI mediumURI) {
        super(mediumURI);
    }

    public Picture() {
    }
}
