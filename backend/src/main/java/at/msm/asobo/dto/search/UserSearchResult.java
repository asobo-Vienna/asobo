package at.msm.asobo.dto.search;

import java.util.UUID;

public class UserSearchResult {
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

    public UserSearchResult() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAboutMe() {
        return aboutMe;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCreatedEventsCount() {
        return createdEventsCount;
    }

    public void setCreatedEventsCount(int createdEventsCount) {
        this.createdEventsCount = createdEventsCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
