package at.msm.asobo.entities.media;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.UUID;

@Entity
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private ArrayList<Medium> media;

    public Gallery() {
    }

    public Gallery(UUID id, String name, ArrayList<Medium> media) {
        this.id = id;
        this.name = name;
        this.media = media;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Medium> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Medium> media) {
        this.media = media;
    }

}
