package at.msm.asobo.entities.media;

import jakarta.persistence.Entity;

import java.net.URI;

@Entity
public class Video extends Medium{

    public Video(URI mediumURI){
        super(mediumURI);
    }

    public Video() {
    }
}
