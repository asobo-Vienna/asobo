package at.msm.asobo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginDTO {

    @NotBlank(message = "Username or Email must not be blank")
    @Size(min = 3, max = 50, message = "Must be between 3 and 50 characters")
    private String identifier;

    @NotBlank(message = "Password must be provided")
    private String password;

    private boolean rememberMe;

    // TODO: decide if inactive users can log in
    private boolean active;

    public UserLoginDTO(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public UserLoginDTO() {}

    public String getIdentifier() {
        return this.identifier;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isRememberMe() { return this.rememberMe; }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
