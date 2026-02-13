package at.msm.asobo.dto.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class UserPublicDTO {

  private UUID id;

  private String username;

  private String firstName;

  private String surname;

  private String email;

  private String aboutMe;

  private Instant registerDate;

  private Instant modificationDate;

  private boolean isActive;

  private String pictureURI;

  private String location;

  private String salutation;

  public UserPublicDTO() {}

  public void setId(UUID id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public String getAboutMe() {
    return this.aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPictureURI() {
    return this.pictureURI;
  }

  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public void setRegisterDate(Instant registerDate) {
    this.registerDate = registerDate;
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

  public Instant getModificationDate() {
    return this.modificationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserPublicDTO that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
