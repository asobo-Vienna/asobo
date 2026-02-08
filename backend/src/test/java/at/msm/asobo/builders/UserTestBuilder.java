package at.msm.asobo.builders;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public class UserTestBuilder {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String surname;
    private String salutation;
    private String location;
    private Boolean isActive;
    private String aboutMe;
    private String password;

    public UserTestBuilder() {
        this.id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        this.username = "testuser";
        this.email = "test@example.com";
        this.firstName = "Test";
        this.surname = "User";
        this.salutation = "Mr.";
        this.location = "Vienna";
        this.isActive = true;
        this.aboutMe = "I am him";
        this.password = "password";
    }

    public UserTestBuilder fromUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.surname = user.getSurname();
        this.salutation = user.getSalutation();
        this.location = user.getLocation();
        this.isActive = user.getIsActive();
        this.aboutMe = user.getAboutMe();
        this.password = user.getPassword();
        return this;
    }

    public UserTestBuilder withoutId() {
        return this;
    }

    public UserTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTestBuilder withUsernameAndEmail(String username) {
        this.username = username;
        this.email = username + "@example.com";
        return this;
    }

    public UserTestBuilder withUsernameAndEmail(String username, String email) {
        this.username = username;
        this.email = email;
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

    public User buildUserEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setSalutation(this.salutation);
        user.setPassword(this.password);

        return user;
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

    public UserPrincipal buildUserPrincipal() {
        return new UserPrincipal(
                this.id,
                this.username,
                this.password,
                List.of()
        );
    }
}
