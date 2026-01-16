package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.interfaces.EventAdminChecker;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserAuthorizationService {
    private final UserRepository userRepository;
    // using interface to avoid circular dependency and allow testing
    private final EventAdminChecker eventAdminChecker;
    private final EventService eventService;

    public UserAuthorizationService(UserRepository userRepository,
                                    EventAdminChecker eventAdminChecker,
                                    EventService eventService) {

        this.userRepository = userRepository;
        this.eventAdminChecker = eventAdminChecker;
        this.eventService = eventService;
    }

    public boolean canUpdateEntity(UUID targetUserId, UUID loggedInUserId) {
        return targetUserId.equals(loggedInUserId)
                || this.eventAdminChecker.isUserAdminOfEvent(targetUserId, loggedInUserId)
                || this.hasAdminRole(loggedInUserId);
    }

    private boolean hasAdminRole(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }

    public boolean canManageEvent(UUID eventId, UUID loggedInUserId) {
        return eventAdminChecker.isUserAdminOfEvent(eventId, loggedInUserId)
                || this.isUserEventCreator(eventId, loggedInUserId)
                || hasAdminRole(loggedInUserId);
    }

    private boolean isUserEventCreator(UUID eventId, UUID userId) {
        Event event = eventService.getEventById(eventId);
        return event.getCreator().getId().equals(userId);
    }
}
