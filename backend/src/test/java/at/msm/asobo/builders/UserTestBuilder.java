package at.msm.asobo.builders;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;

import java.util.UUID;

public class UserTestBuilder {
    private UUID id = UUID.randomUUID();
    private String username = "testuser";
    private String email = "test@example.com";
    private String firstName = "Test";
    private String surname = "User";
    private String salutation = "Mr.";
    private String location = "Vienna";
    private Boolean isActive = true;
    private String aboutMe = "I am him";
    private String password = "password";

    public UserTestBuilder withoutId() {
        return this;
    }

    public UserTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUsername(String username) {
        this.username = username;
        this.email = username + "@example.com";
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public UserTestBuilder withSalutation(String salutation) {
        this.salutation = salutation;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public UserTestBuilder withIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public UserTestBuilder withAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
        return this;
    }

    public UserPublicDTO buildUserPublicDTO() {
        UserPublicDTO user = new UserPublicDTO();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setSalutation(this.salutation);
        return user;
    }

    public UserRegisterDTO buildUserRegisterDTO() {
        UserRegisterDTO user = new UserRegisterDTO();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setSalutation(this.salutation);
        user.setPassword(this.password);
        return user;
    }

    public UserUpdateDTO buildUserUpdateDTO() {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setSalutation(this.salutation);
        user.setPassword(this.password);
        return user;
    }

    public LoginResponseDTO buildLoginResponseDTO() {
        UserPublicDTO user = new UserPublicDTO();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setSalutation(this.salutation);

        return new LoginResponseDTO("any-token", user);
    }
}
