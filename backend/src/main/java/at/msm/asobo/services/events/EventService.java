package at.msm.asobo.services.events;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.services.files.FileValidationService;
import at.msm.asobo.utils.PatchUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final FileValidationService fileValidationService;
    private final EventAdminService eventAdminService;
    private final FileStorageProperties fileStorageProperties;
    private final EventDTOEventMapper eventDTOEventMapper;
    private final UserDTOUserMapper userDTOUserMapper;

    public EventService(
            EventRepository eventRepository,
            UserService userService,
            FileStorageService fileStorageService,
            FileValidationService fileValidationService,
            EventAdminService eventAdminService,
            FileStorageProperties fileStorageProperties,
            EventDTOEventMapper eventDTOEventMapper,
            UserDTOUserMapper userDTOUserMapper) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.fileValidationService = fileValidationService;
        this.eventAdminService = eventAdminService;
        this.fileStorageProperties = fileStorageProperties;
        this.eventDTOEventMapper = eventDTOEventMapper;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    public List<EventSummaryDTO> getAllEvents() {
        List<Event> allEvents = this.eventRepository.findAll();
        return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(allEvents);
    }

    public List<Event> getAllEventEntities() {
        return this.eventRepository.findAll();
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
            events = eventRepository.findByParticipants_Id(participantId);
        } else if (isPrivate) {
            events = eventRepository.findByParticipants_IdAndIsPrivateEventTrue(participantId);
        } else {
            events = eventRepository.findByParticipants_IdAndIsPrivateEventFalse(participantId);
        }
        return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
    }

    public Page<EventSummaryDTO> getEventsByParticipantIdPaginated(UUID participantId, Boolean isPrivate, Pageable pageable) {
        Page<Event> events;

        if (isPrivate == null) {
            events = eventRepository.findByParticipants_Id(participantId, pageable);
        } else if (isPrivate) {
            events = eventRepository.findByParticipants_IdAndIsPrivateEventTrue(participantId, pageable);
        } else {
            events = eventRepository.findByParticipants_IdAndIsPrivateEventFalse(participantId, pageable);
        }
        return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
    }

    public List<EventSummaryDTO> getEventsByDate(LocalDateTime date) {
        List<Event> events = this.eventRepository.findEventsByDate(date);
        return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
    }

    public List<EventSummaryDTO> getEventsByLocation(String location) {
        List<Event> events = this.eventRepository.findEventsByLocation(location);
        return this.eventDTOEventMapper.mapEventsToEventSummaryDTOs(events);
    }

    public EventDTO addNewEvent(EventCreationDTO eventCreationDTO) {
        if (eventCreationDTO.getEventAdmins() == null || eventCreationDTO.getEventAdmins().isEmpty()) {
            eventCreationDTO.setEventAdmins(Set.of(eventCreationDTO.getCreator()));
        }

        Event newEvent = this.eventDTOEventMapper.mapEventCreationDTOToEvent(eventCreationDTO);

        Event savedEvent = this.eventRepository.save(newEvent);
        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public Event getEventById(UUID id) {
        Event event = this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return event;
    }

    public EventDTO getEventDTOById(UUID id) {
        Event event = this.getEventById(id);
        return this.eventDTOEventMapper.mapEventToEventDTO(event);
    }

    public List<EventDTO> getEventsByTitle(String title) {
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

    public EventDTO updateEventById(UUID eventId, UserPrincipal userPrincipal, EventUpdateDTO eventUpdateDTO) {
        Event existingEvent = this.getEventById(eventId);
        User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

        boolean canUpdateEvent = this.eventAdminService.canManageEvent(existingEvent, loggedInUser);
        if (!canUpdateEvent) {
            throw new UserNotAuthorizedException("You are not allowed to update this event");
        }

        PatchUtils.copyNonNullProperties(eventUpdateDTO, existingEvent, "picture", "participants");

        this.handleEventPictureUpdate(eventUpdateDTO.getPicture(), existingEvent);

        if (eventUpdateDTO.getParticipants() != null) {
            existingEvent.setParticipants(
                    this.userDTOUserMapper.mapUserPublicDTOsToUsers(eventUpdateDTO.getParticipants())
            );
        }

        Event savedEvent = this.eventRepository.save(existingEvent);
        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    private void handleEventPictureUpdate(MultipartFile picture, Event event) {
        if (picture == null || picture.isEmpty()) {
            return;
        }

        this.fileValidationService.validateImage(picture);

        if (event.getPictureURI() != null) {
            this.fileStorageService.delete(event.getPictureURI());
        }

        String pictureURI = this.fileStorageService.store(picture, this.fileStorageProperties.getEventCoverPictureSubfolder());
        event.setPictureURI(pictureURI);
    }
}
