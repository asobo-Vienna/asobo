package at.msm.asobo.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private String password;

    private String oldPassword;

    private LocalDateTime registerDate;

    private boolean isActive;

    private String pictureURI;

    private String location;

    private String salutation;

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public UserDTO() {
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

    public String getLocation() {
        return location;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getPassword() {
        return this.password;
    }
}
