package at.msm.asobo.entities;

import at.msm.asobo.dto.UserRegisterDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
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

    @NotBlank(message = "Password is mandatory")
    private String password;

    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<Event> createdEvents;

    @ManyToMany(mappedBy = "participants")
    private List<Event> attendedEvents;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<UserComment> comments;

    private URI pictureURI;

    private String location;

    @CreationTimestamp
    private LocalDateTime registerDate;

    private boolean isActive;

    private String salutation;

    public User(){
    }

    public User(UserRegisterDTO registerDTO) {
        this.username = registerDTO.getUsername();
        this.email = registerDTO.getEmail();
        this.password = registerDTO.getPassword();
        this.location = "default";
        this.registerDate = LocalDateTime.now();
        this.isActive = true;
        try {
            this.pictureURI = new URI("resources/static/images/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.salutation = registerDTO.getSalutation();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public URI getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(URI pictureURI) {
        this.pictureURI = pictureURI;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
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
}
