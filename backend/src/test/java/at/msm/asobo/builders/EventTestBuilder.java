package at.msm.asobo.builders;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.mappers.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class EventTestBuilder {
  private static final UUID FIXED_EVENT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 1, 1, 12, 0);
  private static final String DEFAULT_PICTURE_URI =
      "http://localhost:8080/uploads/event-cover-pictures/event-cover-default.svg";

  private UUID id;
  private User creator;
  private Set<User> eventAdmins;
  private Set<User> participants;
  private String title;
  private String description;
  private LocalDateTime date;
  private String location;
  private String pictureURI;
  private Instant creationDate;
  private Instant modificationDate;
  private List<UserComment> comments;
  private List<Medium> media;
  private boolean isPrivateEvent;

  private final UserDTOUserMapper userDTOUserMapper;
  private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
  private final MediumDTOMediumMapper mediumDTOMediumMapper;
  private final EventDTOEventMapper eventDTOEventMapper;

  public EventTestBuilder() {
    this.id = FIXED_EVENT_ID;
    this.creator = defaultCreator();

    this.eventAdmins = new HashSet<>(Set.of(creator));
    this.participants = new HashSet<>(Set.of(creator));

    this.title = "Test event";
    this.description = "Best event ever!";

    this.date = FIXED_TIME.plusDays(7);
    this.creationDate = FIXED_TIME.toInstant(ZoneOffset.UTC);
    this.modificationDate = FIXED_TIME.plusHours(5).toInstant(ZoneOffset.UTC);

    this.location = "Vienna";
    this.pictureURI = DEFAULT_PICTURE_URI;

    this.comments = new ArrayList<>();
    this.media = new ArrayList<>();

    this.isPrivateEvent = false;

    this.userDTOUserMapper = new UserDTOUserMapperImpl();
    this.userCommentDTOUserCommentMapper = new UserCommentDTOUserCommentMapper();
    this.mediumDTOMediumMapper = new MediumDTOMediumMapperImpl();
    this.eventDTOEventMapper =
        new EventDTOEventMapper(
            this.userDTOUserMapper,
            this.userCommentDTOUserCommentMapper,
            this.mediumDTOMediumMapper);
  }

  public EventTestBuilder fromEvent(Event event) {
    this.id = event.getId();
    this.creator = event.getCreator();
    this.eventAdmins = event.getEventAdmins();
    this.participants = event.getParticipants();
    this.title = event.getTitle();
    this.description = event.getDescription();
    this.date = event.getDate();
    this.creationDate = event.getCreationDate();
    this.modificationDate = event.getModificationDate();
    this.location = event.getLocation();
    this.pictureURI = event.getPictureURI();
    this.comments = event.getComments();
    this.media = event.getMedia();
    this.isPrivateEvent = event.isPrivateEvent();
    return this;
  }

  /*public EventTestBuilder fromEventCreationDTO(EventCreationDTO creationDTO) {
      this.id = creationDTO.getId();
      this.creator = this.userDTOUserMapper.mapUserPublicDTOToUser(creationDTO.getCreator());
      this.eventAdmins = this.userDTOUserMapper.mapUserPublicDTOsToUsers(creationDTO.getEventAdmins());
      this.participants = this.userDTOUserMapper.mapUserPublicDTOsToUsers(creationDTO.getParticipants());
      this.title = creationDTO.getTitle();
      this.description = creationDTO.getDescription();
      this.date = creationDTO.getDate();
      this.creationDate = creationDTO.getCreationDate();
      this.modificationDate = creationDTO.getModificationDate();
      this.location = creationDTO.getLocation();
      this.comments = creationDTO.getComments();
      this.media = creationDTO.getMedia();
      this.isPrivateEvent = creationDTO.isPrivate();
      return this;
  }*/

  public User defaultCreator() {
    return new UserTestBuilder()
        .withId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
        .withUsernameAndEmail("creator")
        .buildUserEntity();
  }

  public EventTestBuilder withoutId() {
    return this;
  }

  public EventTestBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public EventTestBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public EventTestBuilder withCreator(User creator) {
    this.creator = creator;
    return this;
  }

  public EventTestBuilder withEventAdmins(Set<User> eventAdmins) {
    this.eventAdmins = eventAdmins;
    return this;
  }

  public EventTestBuilder withParticipants(Set<User> participants) {
    this.participants = participants;
    return this;
  }

  public EventTestBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public EventTestBuilder withDate(LocalDateTime date) {
    this.date = date;
    return this;
  }

  public EventTestBuilder withLocation(String location) {
    this.location = location;
    return this;
  }

  public EventTestBuilder withPictureURI(String pictureURI) {
    this.pictureURI = pictureURI;
    return this;
  }

  public EventTestBuilder withCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  public EventTestBuilder withModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
    return this;
  }

  public EventTestBuilder withComments(List<UserComment> comments) {
    this.comments = comments;
    return this;
  }

  public EventTestBuilder withMedia(List<Medium> media) {
    this.media = media;
    return this;
  }

  public EventTestBuilder withIsPrivateEvent(boolean isPrivateEvent) {
    this.isPrivateEvent = isPrivateEvent;
    return this;
  }

  public Event buildEventEntity() {
    Event event = new Event();
    event.setId(this.id);
    event.setCreator(this.creator);
    event.setDate(this.date);
    event.setDescription(this.description);
    event.setTitle(this.title);
    event.setEventAdmins(this.eventAdmins);
    event.setParticipants(this.participants);
    event.setLocation(this.location);
    event.setPictureURI(this.pictureURI);
    event.setCreationDate(this.creationDate);
    event.setModificationDate(this.modificationDate);
    event.setComments(this.comments);
    event.setMedia(this.media);
    event.setPrivateEvent(this.isPrivateEvent);

    return event;
  }

  public EventDTO buildEventDTO() {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setId(this.id);
    eventDTO.setCreator(this.userDTOUserMapper.mapUserToUserPublicDTO(this.creator));
    eventDTO.setDate(this.date);
    eventDTO.setDescription(this.description);
    eventDTO.setTitle(this.title);
    eventDTO.setEventAdmins(this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.eventAdmins));
    eventDTO.setParticipants(this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.participants));
    eventDTO.setLocation(this.location);
    eventDTO.setPictureURI(this.pictureURI);
    eventDTO.setCreationDate(this.creationDate);
    eventDTO.setModificationDate(this.modificationDate);
    eventDTO.setComments(
        this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(this.comments));
    eventDTO.setMedia(this.mediumDTOMediumMapper.mapMediaToMediaDTOList(this.media));
    eventDTO.setIsPrivate(this.isPrivateEvent);

    return eventDTO;
  }

  public EventCreationDTO buildEventCreationDTO() {
    EventCreationDTO eventCreationDTO = new EventCreationDTO();
    eventCreationDTO.setId(this.id);
    eventCreationDTO.setTitle(this.title);
    eventCreationDTO.setDescription(this.description);
    eventCreationDTO.setLocation(this.location);
    eventCreationDTO.setPrivate(this.isPrivateEvent);
    eventCreationDTO.setDate(this.date);
    eventCreationDTO.setCreationDate(this.creationDate);
    eventCreationDTO.setModificationDate(this.modificationDate);
    eventCreationDTO.setCreator(this.userDTOUserMapper.mapUserToUserPublicDTO(this.creator));
    eventCreationDTO.setEventAdmins(
        this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.eventAdmins));
    eventCreationDTO.setParticipants(
        this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.participants));
    eventCreationDTO.setComments(
        this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(this.comments));
    eventCreationDTO.setMedia(this.mediumDTOMediumMapper.mapMediaToMediaDTOList(this.media));

    return eventCreationDTO;
  }

  public EventUpdateDTO buildEventUpdateDTO() {
    EventUpdateDTO eventUpdateDTO = new EventUpdateDTO();
    eventUpdateDTO.setTitle(this.title);
    eventUpdateDTO.setDescription(this.description);
    eventUpdateDTO.setLocation(this.location);
    eventUpdateDTO.setPrivate(this.isPrivateEvent);
    eventUpdateDTO.setDate(this.date);
    eventUpdateDTO.setParticipants(
        this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.participants));
    eventUpdateDTO.setEventAdmins(
        this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.eventAdmins));

    return eventUpdateDTO;
  }

  public EventSummaryDTO buildEventSummaryDTO() {
    EventSummaryDTO eventDTO = new EventSummaryDTO();
    eventDTO.setId(this.id);
    eventDTO.setCreator(this.userDTOUserMapper.mapUserToUserPublicDTO(this.creator));
    eventDTO.setDate(this.date);
    eventDTO.setDescription(this.description);
    eventDTO.setTitle(this.title);
    eventDTO.setEventAdminCount(this.eventAdmins.size());
    eventDTO.setParticipantCount(this.participants.size());
    eventDTO.setLocation(this.location);
    eventDTO.setPictureURI(this.pictureURI);
    eventDTO.setCreationDate(this.creationDate);
    eventDTO.setModificationDate(this.modificationDate);
    eventDTO.setCommentCount(this.comments.size());
    eventDTO.setMediaCount(this.media.size());
    eventDTO.setIsPrivate(this.isPrivateEvent);

    return eventDTO;
  }
}
