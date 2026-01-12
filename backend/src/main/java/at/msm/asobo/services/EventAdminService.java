package at.msm.asobo.services;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.EventAdminException;
import at.msm.asobo.exceptions.UserNotAuthorizedException;
import at.msm.asobo.interfaces.EventAdminChecker;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventAdminService implements EventAdminChecker {

    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserService userService;
    private final UserDTOUserMapper userDTOUserMapper;
    private final EventDTOEventMapper eventDTOEventMapper;


    public EventAdminService(EventRepository eventRepository,
                             EventService eventService,
                             UserService userService,
                             UserDTOUserMapper userDTOUserMapper, EventDTOEventMapper eventDTOEventMapper) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.userDTOUserMapper = userDTOUserMapper;
        this.userService = userService;
        this.eventDTOEventMapper = eventDTOEventMapper;
    }

    public List<UserPublicDTO> getAllEventAdminsByEventId(UUID eventId) {
        Event event = this.eventService.getEventById(eventId);
        return userDTOUserMapper.mapUsersToUserPublicDTOs(event.getEventAdmins());
    }

    @Override
    public boolean isUserAdminOfEvent(UUID eventId, UUID userId) {
        Event event = this.eventService.getEventById(eventId);
        User user = this.userService.getUserById(userId);
        List<User> eventAdmins = event.getEventAdmins();

        return eventAdmins.contains(user);
    }

    public EventDTO addAdminToEvent(UUID eventId, UUID userId) {
        Event event = this.eventService.getEventById(eventId);
        User userToAdd = this.userService.getUserById(userId);
        List<User> eventAdmins = event.getEventAdmins();

        if (eventAdmins.contains(userToAdd)) {
            throw new EventAdminException("User is already an admin of this event");
        }
        eventAdmins.add(userToAdd);
        event.setEventAdmins(eventAdmins);
        Event savedEvent = eventRepository.save(event);

        return eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public EventDTO removeAdminFromEvent(UUID eventId, UUID userId) {
        Event event = this.eventService.getEventById(eventId);
        User user = this.userService.getUserById(userId);
        List<User> eventAdmins = event.getEventAdmins();

        if (!eventAdmins.contains(user)) {
            throw new EventAdminException("User is not an admin of this event");
        }
        eventAdmins.remove(user);
        event.setEventAdmins(eventAdmins);
        Event savedEvent = eventRepository.save(event);

        return eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }
}
