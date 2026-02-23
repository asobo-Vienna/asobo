package at.msm.asobo.services.events;

import at.msm.asobo.dto.user.UserBasicDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventAdminException;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.AccessControlService;
import at.msm.asobo.services.UserService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EventAdminService {

  private final EventRepository eventRepository;
  private final UserService userService;
  private final AccessControlService accessControlService;
  private final UserDTOUserMapper userDTOUserMapper;

  public EventAdminService(
      EventRepository eventRepository,
      UserService userService,
      AccessControlService accessControlService,
      UserDTOUserMapper userDTOUserMapper) {
    this.eventRepository = eventRepository;
    this.userService = userService;
    this.accessControlService = accessControlService;
    this.userDTOUserMapper = userDTOUserMapper;
  }

  public Set<UserPublicDTO> getAllEventAdminsByEventId(UUID eventId) {
    Event event = this.getEventById(eventId);
    return this.userDTOUserMapper.mapUsersToUserPublicDTOs(event.getEventAdmins());
  }

  public Set<UserBasicDTO> addAdminsToEvent(
      UUID eventId, Set<UUID> userIds, UserPrincipal loggedInUserPrincipal) {
    Event event = this.getEventById(eventId);
    User loggedInUser = this.userService.getUserById(loggedInUserPrincipal.getUserId());

    if (!this.canManageEvent(event, loggedInUser.getId())) {
      throw new UserNotAuthorizedException(
          "You are not authorized to add event admins to this event");
    }

    Set<User> usersToAdd = this.userService.getUsersByIds(userIds);

    event.getEventAdmins().addAll(usersToAdd);
    Event savedEvent = this.eventRepository.save(event);

    return this.userDTOUserMapper.mapUsersToUserBasicDTOs(savedEvent.getEventAdmins());
  }

  public Set<UserBasicDTO> removeAdminsFromEvent(
      UUID eventId, Set<UUID> userIds, UserPrincipal loggedInUserPrincipal) {
    Event event = this.getEventById(eventId);
    User loggedInUser = this.userService.getUserById(loggedInUserPrincipal.getUserId());
    Set<User> usersToRemove = this.userService.getUsersByIds(userIds);

    if (!this.canManageEvent(event, loggedInUser.getId())) {
      throw new UserNotAuthorizedException(
          "You are not authorized to remove event admins from this event");
    }

    if (usersToRemove.contains(event.getCreator())) {
      throw new EventAdminException("The event creator cannot be removed from event admins");
    }

    if (usersToRemove.contains(loggedInUser)) {
      throw new EventAdminException("You cannot remove yourself from event admins");
    }

    event.getEventAdmins().removeAll(usersToRemove);
    Event savedEvent = this.eventRepository.save(event);

    return this.userDTOUserMapper.mapUsersToUserBasicDTOs(savedEvent.getEventAdmins());
  }

  public boolean canManageEvent(Event event, UUID loggedInUserId) {
    return this.accessControlService.hasAdminRole(loggedInUserId)
        || this.isUserAdminOfEvent(event, loggedInUserId);
  }

  private boolean isUserAdminOfEvent(Event event, UUID userId) {
    if (this.isUserEventCreator(event, userId)) {
      return true;
    }

    Set<UUID> eventAdminIds =
        event.getEventAdmins().stream().map(user -> user.getId()).collect(Collectors.toSet());
    return eventAdminIds.contains(userId);
  }

  private boolean isUserEventCreator(Event event, UUID userId) {
    return event.getCreator().getId().equals(userId);
  }

  private Event getEventById(UUID id) {
    return this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
  }
}
