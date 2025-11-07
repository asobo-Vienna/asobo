package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.mappers.*;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.services.files.FileStorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
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
    private final EventDTOEventMapper eventDTOEventMapper;
    private final UserDTOUserMapper userDTOUserMapper;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
    private final MediumDTOMediumMapper mediumDTOMediumMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;

    @Value("${app.file-storage.event-coverpicture-subfolder}")
    private String eventCoverPictureSubfolder;

    public EventService(EventRepository eventRepository,
                        UserService userService,
                        EventDTOEventMapper eventDTOEventMapper,
                        FileStorageService fileStorageService,
                        FileStorageProperties fileStorageProperties,
                        UserDTOUserMapper userDTOUserMapper,
                        UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
                        MediumDTOMediumMapper mediumDTOMediumMapper) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.eventDTOEventMapper = eventDTOEventMapper;

        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
        this.mediumDTOMediumMapper = mediumDTOMediumMapper;
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = this.eventRepository.findAll();
        return this.eventDTOEventMapper.mapEventsToEventDTOs(events);
    }

    public List<EventDTO> getEventsByDate(LocalDateTime date) {
        List<Event> events = this.eventRepository.findEventsByDate(date);
        return this.eventDTOEventMapper.mapEventsToEventDTOs(events);
    }

    public List<EventDTO> getEventsByLocation(String location) {
        List<Event> events = this.eventRepository.findEventsByLocation(location);
        return this.eventDTOEventMapper.mapEventsToEventDTOs(events);
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

    public EventDTO deleteEventById(UUID id) {
        Event eventToDelete = this.getEventById(id);
        this.fileStorageService.delete(eventToDelete.getPictureURI());
        this.eventRepository.delete(eventToDelete);
        return this.eventDTOEventMapper.mapEventToEventDTO(eventToDelete);
    }

    public EventDTO updateEvent(EventUpdateDTO eventUpdateDTO) {
        Event existingEvent = this.getEventById(eventUpdateDTO.getId());

        existingEvent.setTitle(eventUpdateDTO.getTitle());
        existingEvent.setDescription(eventUpdateDTO.getDescription());
        existingEvent.setLocation(eventUpdateDTO.getLocation());
        existingEvent.setDate(eventUpdateDTO.getDate());
        existingEvent.setPrivate(eventUpdateDTO.isPrivate());

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
