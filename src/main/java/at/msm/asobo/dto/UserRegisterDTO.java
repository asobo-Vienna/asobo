package at.msm.asobo.dto;

import at.msm.asobo.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;

public class UserRegisterDTO {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private URI pictureURI;

    private String location;

    private String salutation;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.pictureURI = user.getPictureURI();
        this.location = user.getLocation();
        this.salutation = user.getSalutation();
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public URI getPictureURI() {
        return this.pictureURI;
    }

    public String getLocation() {
        return this.location;
    }

    public String getSalutation() {
        return salutation;
    }
}
