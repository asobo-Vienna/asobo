package at.msm.asobo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UserRegisterDTO {

    @NotBlank(message = "Username is mandatory for user registration")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "First name is mandatory for user registration")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotBlank(message = "Surname is mandatory for user registration")
    @Size(min = 1, max = 80, message = "First name must be between 1 and 80 characters")
    private String surname;

    @NotBlank(message = "Email is mandatory for user registration")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory for user registration")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    //@NotBlank(message = "Password confirmation is required")
    //private String passwordConf;

    private MultipartFile profilePicture;

    private String location;

    @NotBlank(message = "Salutation is mandatory for user registration")
    private String salutation;

    private String salutationOther;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*public String getPasswordConf() {
        return passwordConf;
    }

    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }*/

    public MultipartFile getProfilePicture() {
        return profilePicture;
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

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getSalutationOther() {
        return salutationOther;
    }

    public void setSalutationOther(String salutationOther) {
        this.salutationOther = salutationOther;
    }
}
