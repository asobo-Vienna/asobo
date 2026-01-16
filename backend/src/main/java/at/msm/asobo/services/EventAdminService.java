package at.msm.asobo.services;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventAdminException;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventAdminService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserPrivilegeService userPrivilegeService;
    private final UserDTOUserMapper userDTOUserMapper;
    private final EventDTOEventMapper eventDTOEventMapper;

    public EventAdminService(EventRepository eventRepository,
                             UserService userService,
                             UserPrivilegeService userPrivilegeService,
                             UserDTOUserMapper userDTOUserMapper,
                             EventDTOEventMapper eventDTOEventMapper) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.userPrivilegeService = userPrivilegeService;
        this.userDTOUserMapper = userDTOUserMapper;
        this.eventDTOEventMapper = eventDTOEventMapper;
    }

    public Set<UserPublicDTO> getAllEventAdminsByEventId(UUID eventId) {
        Event event = this.getEventById(eventId);
        return userDTOUserMapper.mapUsersToUserPublicDTOs(event.getEventAdmins());
    }

    public EventDTO addAdminsToEvent(UUID eventId, Set<UUID> userIds, UUID loggedInUserId) {
        Event event = this.getEventById(eventId);
        Set<User> usersToAdd = this.userService.getUsersByIds(userIds);

        if (!this.canManageEvent(event, loggedInUserId)) {
            throw new UserNotAuthorizedException("You are not authorized to add event admins");
        }

        Set<User> eventAdmins = event.getEventAdmins();
        eventAdmins.addAll(usersToAdd);

        event.setEventAdmins(eventAdmins);
        Event savedEvent = eventRepository.save(event);

        return eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public EventDTO removeAdminsFromEvent(UUID eventId, Set<UUID> userIds, UUID loggedInUserId) {
        Event event = this.getEventById(eventId);
        Set<User> usersToRemove = this.userService.getUsersByIds(userIds);

        if (!this.canManageEvent(event, loggedInUserId)) {
            throw new UserNotAuthorizedException("You are not authorized to remove event admins");
        }

        Set<User> eventAdmins = event.getEventAdmins();
        eventAdmins.removeAll(usersToRemove);
        event.setEventAdmins(eventAdmins);

        Event savedEvent = eventRepository.save(event);

        return eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public boolean canManageEvent(Event event, UUID loggedInUserId) {
        return this.isUserAdminOfEvent(event, loggedInUserId)
                || this.userPrivilegeService.hasAdminRole(loggedInUserId);
    }

    private boolean isUserAdminOfEvent(Event event, UUID userId) {
        User user = this.userService.getUserById(userId);

        if (this.isUserEventCreator(event, userId)) {
            return true;
        }

        Set<User> eventAdmins = event.getEventAdmins();
        return eventAdmins.contains(user);
    }

    private boolean isUserEventCreator(Event event,  UUID userId) {
        return event.getCreator().getId().equals(userId);
    }

    private Event getEventById(UUID id) {
        Event event = this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return event;
    }
}
