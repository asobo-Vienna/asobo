package at.msm.asobo.dto.user;

import at.msm.asobo.entities.User;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private String password;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    private LocalDateTime registerDate;

    private boolean isActive;

    private URI pictureURI;

    private String location;

    private String salutation;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.registerDate = user.getRegisterDate();
        this.isActive = user.isActive();
        this.pictureURI = user.getPictureURI();
        this.location = user.getLocation();
        this.salutation = user.getSalutation();
    }

    public UUID getId() {
        return this.id;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public boolean isActive() {
        return isActive;
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

    public @NotBlank(message = "Password is mandatory") String getPassword() {
        return this.password;
    }
}
