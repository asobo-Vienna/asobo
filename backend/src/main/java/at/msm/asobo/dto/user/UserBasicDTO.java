package at.msm.asobo.dto.user;

import java.util.Objects;
import java.util.UUID;

public class UserBasicDTO {

  private UUID id;

  private String username;

  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    UserBasicDTO that = (UserBasicDTO) o;
    return Objects.equals(id, that.id) && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }
}
