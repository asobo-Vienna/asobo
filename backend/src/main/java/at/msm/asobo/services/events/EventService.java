package at.msm.asobo.services.events;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.dto.filter.EventFilterDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.specifications.EventSpecification;
import at.msm.asobo.utils.PatchUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class EventService {
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;
  private final EventAdminService eventAdminService;
  private final EventDTOEventMapper eventDTOEventMapper;
  private final UserDTOUserMapper userDTOUserMapper;

  public EventService(
      EventRepository eventRepository,
      UserRepository userRepository,
      FileStorageService fileStorageService,
      EventAdminService eventAdminService,
      EventDTOEventMapper eventDTOEventMapper,
      UserDTOUserMapper userDTOUserMapper) {
    this.eventRepository = eventRepository;
    this.userRepository = userRepository;
    this.fileStorageService = fileStorageService;
    this.eventAdminService = eventAdminService;
    this.eventDTOEventMapper = eventDTOEventMapper;
    this.userDTOUserMapper = userDTOUserMapper;
  }

  public List<EventSummaryDTO> getAllEvents(EventFilterDTO filterDTO) {
    List<Event> filteredEvents = eventRepository.findAll(EventSpecification.withFilters(filterDTO));
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(filteredEvents);
  }

  public Page<EventSummaryDTO> getAllEventsPaginated(EventFilterDTO filterDTO, Pageable pageable) {
    Page<Event> filteredEvents =
        eventRepository.findAll(EventSpecification.withFilters(filterDTO), pageable);
    return this.eventDTOEventMapper.mapEventPageToEventSummaryDTOs(filteredEvents);
  }

  public List<EventSummaryDTO> getAllEvents() {
    List<Event> allEvents = this.eventRepository.findAll();
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(allEvents);
  }

  public Page<EventSummaryDTO> getAllEventsPaginated(Pageable pageable) {
    Page<Event> events = this.eventRepository.findAllEvents(pageable);
    return events.map(this.eventDTOEventMapper::toEventSummaryDTO);
  }

  public List<EventSummaryDTO> getAllPublicEvents() {
    List<Event> events = this.eventRepository.findByIsPrivateEventFalse();
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
  }

  public Page<EventSummaryDTO> getAllPublicEventsPaginated(Pageable pageable) {
    Page<Event> events = this.eventRepository.findByIsPrivateEventFalse(pageable);
    return events.map(this.eventDTOEventMapper::toEventSummaryDTO);
  }

  public List<EventSummaryDTO> getAllPrivateEvents() {
    List<Event> allEvents = this.eventRepository.findByIsPrivateEventTrue();
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(allEvents);
  }

  public Page<EventSummaryDTO> getAllPrivateEventsPaginated(Pageable pageable) {
    Page<Event> events = this.eventRepository.findByIsPrivateEventTrue(pageable);
    return events.map(this.eventDTOEventMapper::toEventSummaryDTO);
  }

  public List<EventSummaryDTO> getEventsByParticipantId(UUID participantId, Boolean isPrivate) {
    List<Event> events;

    if (isPrivate == null) {
      events = eventRepository.findByParticipantsId(participantId);
    } else if (isPrivate) {
      events = eventRepository.findByParticipantsIdAndIsPrivateEventTrue(participantId);
    } else {
      events = eventRepository.findByParticipantsIdAndIsPrivateEventFalse(participantId);
    }
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
  }

  public Page<EventSummaryDTO> getEventsByParticipantIdPaginated(
      UUID participantId, Boolean isPrivate, Pageable pageable) {
    Page<Event> events;

    if (isPrivate == null) {
      events = eventRepository.findByParticipantsId(participantId, pageable);
    } else if (isPrivate) {
      events = eventRepository.findByParticipantsIdAndIsPrivateEventTrue(participantId, pageable);
    } else {
      events = eventRepository.findByParticipantsIdAndIsPrivateEventFalse(participantId, pageable);
    }
    return this.eventDTOEventMapper.mapEventPageToEventSummaryDTOs(events);
  }

  public List<EventSummaryDTO> getEventsByDate(LocalDateTime date) {
    if (date == null) {
      throw new IllegalArgumentException("Date must not be null");
    }
    List<Event> events = this.eventRepository.findEventsByDate(date);
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
  }

  public List<EventSummaryDTO> getEventsByLocation(String location) {
    if (location == null || location.trim().isEmpty()) {
      throw new IllegalArgumentException("Location must not be null or empty");
    }

    List<Event> events = this.eventRepository.findEventsByLocation(location);
    return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
  }

  @Transactional
  public EventDTO addNewEvent(EventCreationDTO eventCreationDTO) {
    Event newEvent = this.eventDTOEventMapper.mapEventCreationDTOToEvent(eventCreationDTO);
    User creator =
        userRepository
            .findById(eventCreationDTO.getCreator().getId())
            .orElseThrow(() -> new RuntimeException("Creator not found"));

    newEvent.setCreator(creator);

    eventRepository.save(newEvent);

    Set<User> admins = new HashSet<>();
    admins.add(creator);

    if (eventCreationDTO.getEventAdmins() != null && !eventCreationDTO.getEventAdmins().isEmpty()) {
      eventCreationDTO
          .getEventAdmins()
          .forEach(
              adminDTO -> {
                User admin =
                    userRepository
                        .findById(adminDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                admins.add(admin);
              });
    }

    newEvent.setEventAdmins(admins);
    eventRepository.save(newEvent);

    return eventDTOEventMapper.mapEventToEventDTO(newEvent);
  }

  public Event getEventById(UUID id) {
    return this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
  }

  public EventDTO getEventDTOById(UUID id, boolean isAuthenticated) {
    Event event = this.getEventById(id);

    boolean hasAccessToEvent = isAuthenticated || !event.getIsPrivateEvent();

    if (!hasAccessToEvent) {
      throw new UserNotAuthorizedException("You cannot access this event");
    }
    return this.eventDTOEventMapper.mapEventToEventDTO(event);
  }

  public List<EventDTO> getEventsByTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Title must not be null or empty");
    }

    List<Event> events = this.eventRepository.findEventsByTitle(title);
    return this.eventDTOEventMapper.mapEventsToEventDTOs(events);
  }

  public Event getEventByPicturePath(String filepath) {
    return this.eventRepository
        .findEventByPictureURI(filepath)
        .orElseThrow(() -> new EventNotFoundException(filepath));
  }

  public EventDTO deleteEventById(UUID eventId, UserPrincipal userPrincipal) {
    Event eventToDelete = this.getEventById(eventId);

    UUID loggedInUserId = userPrincipal.getUserId();
    User loggedInUser =
        this.userRepository
            .findUserByIdAndIsDeletedFalse(loggedInUserId)
            .orElseThrow(() -> new UserNotFoundException(loggedInUserId));

    boolean canDeleteEvent =
        this.eventAdminService.canManageEvent(eventToDelete, loggedInUser.getId());
    if (!canDeleteEvent) {
      throw new UserNotAuthorizedException("You are not allowed to delete this event");
    }

    if (eventToDelete.getPictureURI() != null) {
      this.fileStorageService.delete(eventToDelete.getPictureURI());
    }

    this.eventRepository.delete(eventToDelete);
    return this.eventDTOEventMapper.mapEventToEventDTO(eventToDelete);
  }

  public EventDTO updateEventById(
      UUID eventId, UserPrincipal userPrincipal, EventUpdateDTO eventUpdateDTO) {
    Event existingEvent = this.getEventById(eventId);

    UUID loggedInUserId = userPrincipal.getUserId();
    User loggedInUser =
        this.userRepository
            .findUserByIdAndIsDeletedFalse(loggedInUserId)
            .orElseThrow(() -> new UserNotFoundException(loggedInUserId));

    boolean canUpdateEvent =
        this.eventAdminService.canManageEvent(existingEvent, loggedInUser.getId());
    if (!canUpdateEvent) {
      throw new UserNotAuthorizedException("You are not allowed to update this event");
    }

    // Force lazy collections to initialize before modifying the entity
    existingEvent.getEventAdmins().size();
    existingEvent.getParticipants().size();

    PatchUtils.copyNonNullProperties(
        eventUpdateDTO, existingEvent, "picture", "participants", "eventAdmins");

    if (eventUpdateDTO.getParticipants() != null) {
      existingEvent.setParticipants(
          this.userDTOUserMapper.mapUserPublicDTOsToUsers(eventUpdateDTO.getParticipants()));
    }

    this.eventRepository.save(existingEvent);
    return this.eventDTOEventMapper.mapEventToEventDTO(existingEvent);
  }

  @Transactional
  public void updateEventPicture(UUID eventId, UserPrincipal userPrincipal, MultipartFile picture) {

    Event event = this.getEventById(eventId);
    User user = this.userService.getUserById(userPrincipal.getUserId());

    if (!this.eventAdminService.canManageEvent(event, user)) {
      throw new UserNotAuthorizedException("You are not allowed to update this event");
    }

    this.fileStorageService.handleEventPictureUpdate(picture, event);
    this.eventRepository.save(event);
  }
}
