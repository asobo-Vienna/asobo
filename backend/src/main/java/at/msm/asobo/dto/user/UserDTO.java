package at.msm.asobo.dto.user;

import java.time.Instant;
import java.util.UUID;

public class UserDTO {

  private UUID id;

  private String username;

  private String firstName;

  private String surname;

  private String email;

  private String password;

  private String oldPassword;

  private String aboutMe;

  private Instant registerDate;

  private Instant modificationDate;

  private boolean isActive;

  private String pictureURI;

  private String location;

  private String salutation;

  public UserDTO() {}

  public void setId(UUID id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getAboutMe() {
    return this.aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public String getPictureURI() {
    return pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
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

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public UUID getId() {
    return this.id;
  }

  public Instant getRegisterDate() {
    return this.registerDate;
  }

  public boolean getIsActive() {
    return this.isActive;
  }

  public String getEmail() {
    return this.email;
  }

  public String getUsername() {
    return this.username;
  }

  public String getLocation() {
    return this.location;
  }

  public String getSalutation() {
    return this.salutation;
  }

  public String getPassword() {
    return this.password;
  }
}
