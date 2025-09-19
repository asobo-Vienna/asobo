package at.msm.asobo.dto.auth;

import java.util.UUID;

public class TokenRequestDTO {
    private String username;
    private String password;
    private UUID userID;

    public TokenRequestDTO() {}

    public TokenRequestDTO(UUID userID, String username, String password) {
        this.username = username;
        this.password = password;
        this.userID = userID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
