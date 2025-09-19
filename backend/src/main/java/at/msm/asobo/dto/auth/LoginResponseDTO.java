package at.msm.asobo.dto.auth;

import at.msm.asobo.dto.user.UserPublicDTO;

public class LoginResponseDTO {

    private String token;
    private UserPublicDTO user;

    public LoginResponseDTO(String token, UserPublicDTO user) {
        this.token = token;
        this.user = user;
    }

    // Getters & setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPublicDTO getUser() {
        return user;
    }

    public void setUser(UserPublicDTO user) {
        this.user = user;
    }
}

