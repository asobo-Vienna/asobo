package at.msm.asobo.dto.search;

import java.util.UUID;

public class UserSearchResultDTO {
  private UUID id;
  private String username;
  private String firstName;
  private String surname;
  private String fullName;
  private String aboutMe;
  private String pictureURI;
  private String location;
  private int createdEventsCount;
  private String type = "USER";

  public UserSearchResultDTO() {}

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

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAboutMe() {
    return this.aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
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

  public int getCreatedEventsCount() {
    return this.createdEventsCount;
  }

  public void setCreatedEventsCount(int createdEventsCount) {
    this.createdEventsCount = createdEventsCount;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
