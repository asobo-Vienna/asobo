package at.msm.asobo.dto.event;

import at.msm.asobo.entities.User;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;
import java.util.UUID;

public class EventCreatorDTO {

    private UUID id;
    private String username;
    private URI pictureURI;

    public EventCreatorDTO() {}

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public URI getPictureURI() {
        return this.pictureURI;
    }

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

}
