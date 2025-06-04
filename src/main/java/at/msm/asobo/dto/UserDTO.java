package at.msm.asobo.dto;

import at.msm.asobo.entities.User;
import java.net.URI;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String email;
    private String username;
    private URI pictureURI;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.pictureURI = user.getPictureURI();
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
}
