package at.msm.asobo.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDTO {
    private String accessToken;

    public TokenDTO() {}

    public TokenDTO(@JsonProperty("accessToken") String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
