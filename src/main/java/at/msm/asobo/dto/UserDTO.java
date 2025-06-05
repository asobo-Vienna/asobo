package at.msm.asobo.dto;

import at.msm.asobo.entities.User;

import java.net.URI;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private URI pictureURI;

    private String location;

    private String salutation;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.pictureURI = user.getPictureURI();
        this.location = user.getLocation();
        this.salutation = user.getSalutation();
    }

    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public URI getPictureURI() {
        return this.pictureURI;
    }

    public String getLocation() {
        return location;
    }

    public String getSalutation() {
        return salutation;
    }
}
