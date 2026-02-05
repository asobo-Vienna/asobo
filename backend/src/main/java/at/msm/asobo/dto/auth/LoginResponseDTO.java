package at.msm.asobo.dto.auth;

import at.msm.asobo.dto.user.UserPublicDTO;

public class LoginResponseDTO {

    private String token;
    private UserPublicDTO userDTO;

    public LoginResponseDTO(String token, UserPublicDTO userDTO) {
        this.token = token;
        this.userDTO = userDTO;
    }

    // Getters & setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPublicDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserPublicDTO user) {
        this.userDTO = user;
    }
}

