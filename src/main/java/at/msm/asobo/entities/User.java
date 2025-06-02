package at.msm.asobo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @OneToMany(mappedBy = "creator")
    private List<Event> events;

    @OneToMany(mappedBy = "author")
    private List<UserComment> comments;

    private URI pictureURI;

    private String location;

    @NotNull
    @CreationTimestamp
    private LocalDateTime registerDate;

    private boolean isActive;

    public User(){
    }

    public User(String email, String username, String password) {
        this.email=email;
        this.username=username;
        this.password=password;
        this.location="default";
        this.registerDate=LocalDateTime.now();
        this.id=UUID.randomUUID();
        this.isActive=true;
        try {
            this.pictureURI = new URI("resources/static/images/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



    public User(String email, String username, String password, String location){
        this(email, username, password);
        this.location = location;
    }

    public User(String email, String username, String password, URI pictureURI){
        this(email, username, password);
        this.pictureURI=pictureURI;
    }

    public User(String email, String username, String password, String location, URI pictureURI){
        this(email, username, password);
        this.location = location;
        this.pictureURI=pictureURI;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public UUID getId() {
        return id;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<UserComment> getComments() {
        return comments;
    }

    public void setComments(List<UserComment> comments) {
        this.comments = comments;
    }
}
