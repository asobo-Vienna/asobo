package at.msm.asobo.dto.user;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UserAdminSummaryDTO {
  private UUID id;
  private String username;
  private String email;
  private String firstName;
  private String surname;
  private Instant registerDate;
  private Instant modificationDate;
  private boolean isActive;
  private String pictureURI;
  private String location;
  private Set<RoleDTO> roles;
  private int createdEventsCount;
  private int attendedEventsCount;
  private int commentsCount;

  public UserAdminSummaryDTO() {}

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

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurname() {
    return this.surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public Instant getRegisterDate() {
    return this.registerDate;
  }

  public void setRegisterDate(Instant registerDate) {
    this.registerDate = registerDate;
  }

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Set<RoleDTO> getRoles() {
    return this.roles;
  }

  public void setRoles(Set<RoleDTO> roles) {
    this.roles = roles;
  }

  public int getCreatedEventsCount() {
    return this.createdEventsCount;
  }

  public void setCreatedEventsCount(int createdEventsCount) {
    this.createdEventsCount = createdEventsCount;
  }

  public int getAttendedEventsCount() {
    return this.attendedEventsCount;
  }

  public void setAttendedEventsCount(int attendedEventsCount) {
    this.attendedEventsCount = attendedEventsCount;
  }

  public int getCommentsCount() {
    return this.commentsCount;
  }

  public void setCommentsCount(int commentsCount) {
    this.commentsCount = commentsCount;
  }
}
