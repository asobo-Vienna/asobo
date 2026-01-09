package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.exceptions.UserNotAuthorizedException;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.utils.PatchUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserPrivilegeService userPrivilegeService;
    private final EventDTOEventMapper eventDTOEventMapper;
    private final UserDTOUserMapper userDTOUserMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;

    @Value("${app.file-storage.event-coverpicture-subfolder}")
    private String eventCoverPictureSubfolder;

    public EventService(
            EventRepository eventRepository,
            UserService userService,
            EventDTOEventMapper eventDTOEventMapper,
            FileStorageService fileStorageService,
            FileStorageProperties fileStorageProperties,
            UserDTOUserMapper userDTOUserMapper,
            UserPrivilegeService userPrivilegeService
    ) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.eventDTOEventMapper = eventDTOEventMapper;

        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userPrivilegeService = userPrivilegeService;
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
        User user = this.userService.getUserById(eventCreationDTO.getCreator().getId());

        Event newEvent = this.eventDTOEventMapper.mapEventCreationDTOToEvent(eventCreationDTO);
        newEvent.setCreator(user);

        if (eventCreationDTO.getEventPicture() != null && !eventCreationDTO.getEventPicture().isEmpty()) {
            String fileURI = fileStorageService.store(eventCreationDTO.getEventPicture(), this.eventCoverPictureSubfolder);
            newEvent.setPictureURI(fileURI);
        }

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

    public EventDTO deleteEventById(UUID id, UUID loggedInUserId) {
        Event eventToDelete = this.getEventById(id);

        boolean canDeleteEvent = userPrivilegeService
                .canUpdateEntity(eventToDelete.getCreator().getId(),  loggedInUserId);
        if (!canDeleteEvent) {
            throw new UserNotAuthorizedException("You are not authorized to delete this event");
        }

        this.fileStorageService.delete(eventToDelete.getPictureURI());
        this.eventRepository.delete(eventToDelete);
        return this.eventDTOEventMapper.mapEventToEventDTO(eventToDelete);
    }

    public EventDTO updateEventById(UUID eventId, UUID loggedInUserId, EventUpdateDTO eventUpdateDTO) {
        Event existingEvent = this.getEventById(eventId);

        boolean canUpdateEvent = userPrivilegeService
                .canUpdateEntity(existingEvent.getCreator().getId(), loggedInUserId);
        if (!canUpdateEvent) {
            throw new UserNotAuthorizedException("You are not authorized to update this event");
        }

        PatchUtils.copyNonNullProperties(eventUpdateDTO, existingEvent, "picture", "participants");

        if (eventUpdateDTO.getParticipants() != null) {
            existingEvent.setParticipants(
                    userDTOUserMapper.mapUserPublicDTOsToUsers(eventUpdateDTO.getParticipants())
            );
        }

        MultipartFile picture = eventUpdateDTO.getPicture();
        if (picture != null && !picture.isEmpty()) {
            String pictureURI = this.fileStorageService.store(picture, this.fileStorageProperties.getEventCoverPictureSubfolder());
            existingEvent.setPictureURI(pictureURI);
        }

        Event savedEvent = this.eventRepository.save(existingEvent);
        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }
}
