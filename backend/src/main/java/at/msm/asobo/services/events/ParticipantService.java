package at.msm.asobo.services.events;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.UUID;

@Service
public class ParticipantService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserDTOUserMapper userDTOUserMapper;

    public ParticipantService(UserService userService, EventRepository eventRepository,
                              EventService eventService,
                              UserDTOUserMapper userDTOUserMapper) {
        this.userService = userService;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    // return set of DTOs
    public Set<UserPublicDTO> getAllParticipantsAsDTOsByEventId(UUID eventId) {
        Set<User> participants = this.getAllParticipantsByEventId(eventId);
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(participants);
    }

    // return set of user entities
    private Set<User> getAllParticipantsByEventId(UUID eventId) {
        Event event = this.eventService.getEventById(eventId);
        return event.getParticipants();
    }

    public Set<UserPublicDTO> toggleParticipantInEvent(UUID eventId, UserPrincipal userPrincipal) {
        User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());
        Event event = this.eventService.getEventById(eventId);
        Set<User> participants = event.getParticipants();

        if (participants.contains(loggedInUser)) {
            participants.remove(loggedInUser);
        } else {
            participants.add(loggedInUser);
        }

        this.eventRepository.save(event);
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(participants);
    }
}
