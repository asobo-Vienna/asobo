package at.msm.asobo.services;


import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.UserDTOUserMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    private final UserService userService;
    private final EventService eventService;
    private final UserDTOUserMapper userDTOUserMapper;

    public ParticipantService(UserService userService,
                              EventService eventService,
                              UserDTOUserMapper userDTOUserMapper) {
        this.userService = userService;
        this.eventService = eventService;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    public List<UserPublicDTO> getAllParticipantsByEventId(UUID eventId) {
        Event event = this.eventService.getEventById(eventId);
        List<User> participants = event.getParticipants();
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(participants);
    }

    public UserPublicDTO addParticipantToEvent(UUID eventId, UUID participantId) {
        User participant = this.userService.getUserById(participantId);
        Event event = this.eventService.getEventById(eventId);

        List<User> participants = event.getParticipants();
        participants.add(participant);
        event.setParticipants(participants);

        this.eventService.updateEvent(event);

        return this.userDTOUserMapper.mapUserToUserPublicDTO(participant);
    }
}
