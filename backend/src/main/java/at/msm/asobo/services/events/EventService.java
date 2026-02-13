package at.msm.asobo.services.events;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.utils.PatchUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EventService {
  private final EventRepository eventRepository;
  private final UserService userService;
  private final FileStorageService fileStorageService;
  private final EventAdminService eventAdminService;
  private final EventDTOEventMapper eventDTOEventMapper;
  private final UserDTOUserMapper userDTOUserMapper;

  public EventService(
      EventRepository eventRepository,
      UserService userService,
      FileStorageService fileStorageService,
      EventAdminService eventAdminService,
      EventDTOEventMapper eventDTOEventMapper,
      UserDTOUserMapper userDTOUserMapper) {
    this.eventRepository = eventRepository;
    this.userService = userService;
    this.fileStorageService = fileStorageService;
    this.eventAdminService = eventAdminService;
    this.eventDTOEventMapper = eventDTOEventMapper;
    this.userDTOUserMapper = userDTOUserMapper;
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

  public EventDTO addNewEvent(EventCreationDTO eventCreationDTO) {
    if (eventCreationDTO.getEventAdmins() == null || eventCreationDTO.getEventAdmins().isEmpty()) {
      eventCreationDTO.setEventAdmins(Set.of(eventCreationDTO.getCreator()));
    }

    Event newEvent = this.eventDTOEventMapper.mapEventCreationDTOToEvent(eventCreationDTO);

    this.eventRepository.save(newEvent);
    return this.eventDTOEventMapper.mapEventToEventDTO(newEvent);
  }

  public Event getEventById(UUID id) {
    return this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
  }

  public EventDTO getEventDTOById(UUID id) {
    Event event = this.getEventById(id);
    return this.eventDTOEventMapper.mapEventToEventDTO(event);
  }

  public List<EventDTO> getEventsByTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Title must not be null or empty");
    }

    List<Event> events = this.eventRepository.findEventsByTitle(title);
    return this.eventDTOEventMapper.mapEventsToEventDTOs(events);
  }

  public EventDTO deleteEventById(UUID eventId, UserPrincipal userPrincipal) {
    Event eventToDelete = this.getEventById(eventId);
    User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

    boolean canDeleteEvent = this.eventAdminService.canManageEvent(eventToDelete, loggedInUser);
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
    User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

    boolean canUpdateEvent = this.eventAdminService.canManageEvent(existingEvent, loggedInUser);
    if (!canUpdateEvent) {
      throw new UserNotAuthorizedException("You are not allowed to update this event");
    }

    PatchUtils.copyNonNullProperties(eventUpdateDTO, existingEvent, "picture", "participants");

    this.fileStorageService.handleEventPictureUpdate(eventUpdateDTO.getPicture(), existingEvent);

    if (eventUpdateDTO.getParticipants() != null) {
      existingEvent.setParticipants(
          this.userDTOUserMapper.mapUserPublicDTOsToUsers(eventUpdateDTO.getParticipants()));
    }

    this.eventRepository.save(existingEvent);
    return this.eventDTOEventMapper.mapEventToEventDTO(existingEvent);
  }
}
