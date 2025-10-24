package at.msm.asobo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginDTO {

    @NotBlank(message = "Username or Email must not be blank")
    @Size(min = 3, max = 50, message = "Must be between 3 and 50 characters")
    private String identifier;

    @NotBlank(message = "Password must be provided")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private boolean rememberMe;

    // TODO: decide if inactive users can log in
    private boolean active;

    public String getIdentifier() {
        return this.identifier;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isRememberMe() { return this.rememberMe; }
}
