package at.msm.asobo.dto.auth;

import at.msm.asobo.dto.user.UserPublicDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseDTO {

    private String token;

    @JsonProperty("user")
    private UserPublicDTO userDTO;

    public LoginResponseDTO(String token, UserPublicDTO userDTO) {
        this.token = token;
        this.userDTO = userDTO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPublicDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserPublicDTO userDTO) {
        this.userDTO = userDTO;
    }
}

