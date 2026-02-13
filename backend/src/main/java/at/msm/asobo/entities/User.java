package at.msm.asobo.entities;

import at.msm.asobo.interfaces.PictureEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_user_username", columnList = "username"),
      @Index(name = "idx_user_first_name", columnList = "firstName"),
      @Index(name = "idx_user_surname", columnList = "surname"),
      @Index(name = "idx_user_email", columnList = "email"),
      @Index(name = "idx_user_active", columnList = "isActive")
    })
public class User implements PictureEntity {

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

  private String aboutMe;

  @OneToMany(mappedBy = "creator")
  @JsonIgnore
  private List<Event> createdEvents;

  @ManyToMany(mappedBy = "eventAdmins")
  private List<Event> administeredEvents;

  @ManyToMany(mappedBy = "participants")
  private List<Event> attendedEvents;

  @OneToMany(mappedBy = "author")
  @JsonIgnore
  private List<UserComment> comments;

  @OneToMany(mappedBy = "creator")
  @JsonIgnore
  private List<Medium> media;

  @Column(length = 4096)
  private String pictureURI;

  private String location;

  @CreatedDate private Instant registerDate;

  @LastModifiedDate private Instant modificationDate;

  private boolean isActive = true;

  @NotBlank(message = "Salutation is mandatory")
  private String salutation;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  public User() {
    this.createdEvents = new ArrayList<>();
    this.attendedEvents = new ArrayList<>();
    this.administeredEvents = new ArrayList<>();
    this.comments = new ArrayList<>();
    this.media = new ArrayList<>();
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getOldPassword() {
    return this.oldPassword;
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

  @Override
  public String getPictureURI() {
    return this.pictureURI;
  }

  @Override
  public void setPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
  }

  public boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public Instant getRegisterDate() {
    return this.registerDate;
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

  public UUID getId() {
    return this.id;
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

  public List<Event> getAdministeredEvents() {
    return this.administeredEvents;
  }

  public void setAdministeredEvents(List<Event> administeredEvents) {
    this.administeredEvents = administeredEvents;
  }

  public String getSalutation() {
    return this.salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public Set<Role> getRoles() {
    return this.roles != null ? this.roles : new HashSet<>();
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public List<Medium> getMedia() {
    return this.media;
  }

  public void setMedia(List<Medium> media) {
    this.media = media;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
