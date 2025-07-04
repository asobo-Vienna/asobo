package at.msm.asobo.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserPublicDTO {

    private UUID id;

    private String username;

    private String firstName;

    private String surname;

    private String email;

    private LocalDateTime registerDate;

   //private boolean isActive;

    private String pictureURI;

    private String location;

    private String salutation;

    public UserPublicDTO() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public UUID getId() {
        return this.id;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
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
}
