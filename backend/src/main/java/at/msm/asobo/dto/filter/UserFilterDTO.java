package at.msm.asobo.dto.filter;

import java.util.Set;

public class UserFilterDTO {
  private String firstName;
  private String surname;
  private String location;
  private String country;
  private Boolean isActive;
  private Set<Long> roleIds;

  public UserFilterDTO(
      String firstName,
      String surname,
      String location,
      String country,
      Boolean isActive,
      Set<Long> roleIds) {
    this.firstName = firstName;
    this.surname = surname;
    this.location = location;
    this.country = country;
    this.isActive = isActive;
    this.roleIds = roleIds;
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

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getCountry() {
    return this.country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(Boolean active) {
    this.isActive = active;
  }

  public Set<Long> getRoleIds() {
    return this.roleIds;
  }

  public void setRoleIds(Set<Long> roleIds) {
    this.roleIds = roleIds;
  }
}
