package at.msm.asobo.services;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    private final UserService userService;
    private final EventService eventService;
    private final UserDTOUserMapper userDTOUserMapper;
    private final EventDTOEventMapper eventDTOEventMapper;

    public ParticipantService(UserService userService,
                              EventService eventService,
                              UserDTOUserMapper userDTOUserMapper,
                              EventDTOEventMapper eventDTOEventMapper) {
        this.userService = userService;
        this.eventService = eventService;
        this.userDTOUserMapper = userDTOUserMapper;
        this.eventDTOEventMapper = eventDTOEventMapper;
    }

    // return list of DTOs
    public List<UserPublicDTO> getAllParticipantsAsDTOsByEventId(UUID eventId) {
        List<User> participants = this.getAllParticipantsByEventId(eventId);
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(participants);
    }

    // return list of user entities
    private List<User> getAllParticipantsByEventId(UUID eventId) {
        Event event = this.eventService.getEventById(eventId);
        return event.getParticipants();
    }

    public List<UserPublicDTO> toggleParticipantInEvent(UUID eventId, UUID participantId) {
        User participant = this.userService.getUserById(participantId);
        Event event = this.eventService.getEventById(eventId);
        List<User> participants = event.getParticipants();

        if (participants.contains(participant)) {
            participants.remove(participant);
        } else {
            participants.add(participant);
        }

        event.setParticipants(participants);
        this.eventService.updateEvent(this.eventDTOEventMapper.mapEventToEventUpdateDTO(event));
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(participants);
    }
}
