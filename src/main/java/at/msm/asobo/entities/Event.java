package at.msm.asobo.entities;

import at.msm.asobo.entities.media.Gallery;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Event {

    private ArrayList<User> participants;
    private String name;
    private String description;
    private LocalDateTime date;
    private String location;
    private URI pictureURI;
    private LocalDateTime creationDate;
    private ArrayList<UserComment> comments;
    private Gallery gallery;
    private UUID id;

    public Event(){

    }

    public Event(String name, String description, LocalDateTime date, String location){
        this.name=name;
        this.description=description;
        this.date=date;
        this.location=location;
        this.participants= new ArrayList<User>();
        this.creationDate=LocalDateTime.now();
        this.comments= new ArrayList<UserComment>();
        this.gallery= new Gallery();
        this.id=UUID.randomUUID();
        try {
            this.pictureURI = new URI("resources/static/images/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public URI getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ArrayList<UserComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<UserComment> comments) {
        this.comments = comments;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
