package at.msm.asobo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "First name is mandatory")
    @Column(nullable = false, unique = false)
    private String firstName;

    @NotBlank(message = "Surname is mandatory")
    @Column(nullable = false, unique = false)
    private String surname;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private String oldPassword;

    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<Event> createdEvents;

    @ManyToMany(mappedBy = "participants")
    private List<Event> attendedEvents;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<UserComment> comments;

    @Column(length = 4096)
    private String pictureURI;

    private String location;

    @CreationTimestamp
    private LocalDateTime registerDate;

    private boolean isActive;

    @NotBlank(message = "Salutation is mandatory")
    private String salutation;

    @ManyToMany
    private Set<Role> roles;

    public User(){
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
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

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getRegisterDate() {
        return this.registerDate;
    }

    public UUID getId() {
        return this.id;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Event> getCreatedEvents() {
        return this.createdEvents;
    }

    public void setCreatedEvents(List<Event> events) {
        this.createdEvents = events;
    }

    public List<UserComment> getComments() {
        return this.comments;
    }

    public void setComments(List<UserComment> comments) {
        this.comments = comments;
    }

    public List<Event> getAttendedEvents() {
        return this.attendedEvents;
    }

    public void setAttendedEvents(List<Event> attendedEvents) {
        this.attendedEvents = attendedEvents;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getRole() {
        // TODO
        return "role placeholder";
    }
}
