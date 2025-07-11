package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.services.files.FileStorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventDTOEventMapper eventDTOEventMapper;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Value("${app.file-storage.event-coverpicture-subfolder}")
    private String eventCoverPictureSubfolder;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        EventDTOEventMapper eventDTOEventMapper,
                        UserService userService,
                        FileStorageService fileStorageService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventDTOEventMapper = eventDTOEventMapper;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
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
        // ADD this again as soon as we have logged-in users
        // User user = this.userRepository.findById(eventCreationDTO.getCreator().getId())
                // .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event newEvent = this.eventDTOEventMapper.mapEventCreationDTOToEvent(eventCreationDTO);
        // newEvent.setCreator(user);

        if (eventCreationDTO.getEventPicture() != null && !eventCreationDTO.getEventPicture().isEmpty()) {
            String fileURI = fileStorageService.store(eventCreationDTO.getEventPicture(), this.eventCoverPictureSubfolder);
            newEvent.setPictureURI(fileURI);
        }

        Event savedEvent = this.eventRepository.save(newEvent);
        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public EventDTO addParticipantToEvent(UUID eventId, UUID participantId) {
        User participant = this.userService.getUserById(participantId);
        Event event = this.getEventById(eventId);

        List<User> participants = event.getParticipants();
        participants.add(participant);
        event.setParticipants(participants);

        Event updatedEvent = this.eventRepository.save(event);
        return this.eventDTOEventMapper.mapEventToEventDTO(updatedEvent);
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
        this.eventRepository.delete(eventToDelete);
        return this.eventDTOEventMapper.mapEventToEventDTO(eventToDelete);
    }
}
