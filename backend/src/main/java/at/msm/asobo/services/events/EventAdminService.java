package at.msm.asobo.services.events;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.AccessControlService;
import at.msm.asobo.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class EventAdminService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final AccessControlService accessControlService;
    private final UserDTOUserMapper userDTOUserMapper;
    private final EventDTOEventMapper eventDTOEventMapper;

    public EventAdminService(EventRepository eventRepository,
                             UserService userService,
                             AccessControlService accessControlService,
                             UserDTOUserMapper userDTOUserMapper,
                             EventDTOEventMapper eventDTOEventMapper) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.accessControlService = accessControlService;
        this.userDTOUserMapper = userDTOUserMapper;
        this.eventDTOEventMapper = eventDTOEventMapper;
    }

    public Set<UserPublicDTO> getAllEventAdminsByEventId(UUID eventId) {
        Event event = this.getEventById(eventId);
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(event.getEventAdmins());
    }

    public EventDTO addAdminsToEvent(UUID eventId, Set<UUID> userIds, UserPrincipal loggedInUserPrincipal) {
        Event event = this.getEventById(eventId);
        User loggedInUser = this.userService.getUserById(loggedInUserPrincipal.getUserId());
        Set<User> usersToAdd = this.userService.getUsersByIds(userIds);

        if (!this.canManageEvent(event, loggedInUser)) {
            throw new UserNotAuthorizedException("You are not authorized to add event admins to this event");
        }

        event.getEventAdmins().addAll(usersToAdd);
        Event savedEvent = this.eventRepository.save(event);

        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public EventDTO removeAdminsFromEvent(UUID eventId, Set<UUID> userIds, UserPrincipal loggedInUserPrincipal) {
        Event event = this.getEventById(eventId);
        User loggedInUser = this.userService.getUserById(loggedInUserPrincipal.getUserId());
        Set<User> usersToRemove = this.userService.getUsersByIds(userIds);

        if (!this.canManageEvent(event, loggedInUser)) {
            throw new UserNotAuthorizedException("You are not authorized to remove event admins from this event");
        }

        event.getEventAdmins().removeAll(usersToRemove);
        Event savedEvent = this.eventRepository.save(event);

        return this.eventDTOEventMapper.mapEventToEventDTO(savedEvent);
    }

    public boolean canManageEvent(Event event, User loggedInUser) {
        return this.accessControlService.hasAdminRole(loggedInUser) || this.isUserAdminOfEvent(event, loggedInUser);
    }

    private boolean isUserAdminOfEvent(Event event, User user) {
        if (this.isUserEventCreator(event, user)) {
            return true;
        }

        Set<User> eventAdmins = event.getEventAdmins();
        return eventAdmins.contains(user);
    }

    private boolean isUserEventCreator(Event event, User user) {
        return event.getCreator().getId().equals(user.getId());
    }

    private Event getEventById(UUID id) {
        Event event = this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return event;
    }
}
