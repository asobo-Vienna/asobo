package at.msm.asobo.entities.media;

import java.net.URI;

public abstract class Medium {

    protected URI mediumURI;

    public Medium(URI mediumURI){
        this.mediumURI=mediumURI;
    }

    public URI getMediumURI() {
        return mediumURI;
    }

    public void setMediumURI(URI mediumURI) {
        this.mediumURI = mediumURI;
    }


}
