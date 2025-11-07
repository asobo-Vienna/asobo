package at.msm.asobo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Username must have between 3 and 50 characters")
    private String username;

    @Size(min = 1, max = 50, message = "First name must have between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 80, message = "First name must have between 1 and 80 characters")
    private String surname;

    @Email(message = "Email should be valid")
    private String email;

    private String salutation;

    private String location;

    @Size(min = 6, message = "Password must contain at least 6 characters")
    private String password;

    @Size(min=1, message = "About me must contain at least 1 character")
    private String aboutMe;

    private boolean active;

    private MultipartFile profilePicture;

    public String getUsername() {
        return username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAboutMe() {
        return this.aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }
}
