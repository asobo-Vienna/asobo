package at.msm.asobo.services;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventService;
import at.msm.asobo.services.events.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserDTOUserMapper userDTOUserMapper;

    @InjectMocks
    private ParticipantService participantService;

    private UUID eventId;
    private UUID userId;
    private Event event;
    private User user;
    private UserPrincipal userPrincipal;
    private Set<User> participants;
    private Set<UserPublicDTO> participantDTOs;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setSurname("User");

        event = new Event();
        event.setId(eventId);
        participants = new HashSet<>();
        event.setParticipants(participants);

        userPrincipal = new UserPrincipal(userId, "test@example.com", "password", null);

        participantDTOs = new HashSet<>();
        UserPublicDTO dto = new UserPublicDTO();
        dto.setId(userId);
        dto.setEmail("test@example.com");
        participantDTOs.add(dto);
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_returnsParticipantDTOs() {
        User participant1 = new User();
        participant1.setId(UUID.randomUUID());
        User participant2 = new User();
        participant2.setId(UUID.randomUUID());

        participants.add(participant1);
        participants.add(participant2);

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(participants))
                .thenReturn(participantDTOs);

        Set<UserPublicDTO> result = participantService.getAllParticipantsAsDTOsByEventId(eventId);

        assertNotNull(result);
        assertEquals(participantDTOs, result);
        verify(eventService).getEventById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(participants);
    }

    @Test
    void toggleParticipantInEvent_userNotParticipating_addsUser() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        Set<UserPublicDTO> result = participantService.toggleParticipantInEvent(eventId, userPrincipal);

        assertTrue(participants.contains(user));
        assertEquals(1, participants.size());
        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(participants);
        assertNotNull(result);
    }

    @Test
    void toggleParticipantInEvent_userAlreadyParticipating_removesUser() {
        participants.add(user); // user is already participating

        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(new HashSet<>());
        
        Set<UserPublicDTO> result = participantService.toggleParticipantInEvent(eventId, userPrincipal);

        assertFalse(participants.contains(user));
        assertEquals(0, participants.size());
        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(participants);
        assertNotNull(result);
    }

    @Test
    void toggleParticipantInEvent_multipleParticipants_onlyTogglesLoggedInUser() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        participants.add(otherUser);

        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        participantService.toggleParticipantInEvent(eventId, userPrincipal);

        assertEquals(2, participants.size());
        assertTrue(participants.contains(user));
        assertTrue(participants.contains(otherUser));
        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_eventNotFound_throwsException() {
        when(eventService.getEventById(eventId))
                .thenThrow(new EventNotFoundException(eventId));

        assertThrows(EventNotFoundException.class, () -> {
            participantService.getAllParticipantsAsDTOsByEventId(eventId);
        });

        verify(eventService).getEventById(eventId);
        // check if mapUsersToUserPublicDTOs() has never been called
        verify(userDTOUserMapper, never()).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_emptyParticipantsList_returnsEmptySet() {
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(participants))
                .thenReturn(new HashSet<>());

        Set<UserPublicDTO> result = participantService.getAllParticipantsAsDTOsByEventId(eventId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventService).getEventById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(participants);
    }

    @Test
    void toggleParticipantInEvent_eventNotFound_throwsException() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId))
                .thenThrow(new EventNotFoundException(eventId));

        assertThrows(EventNotFoundException.class, () -> {
            participantService.toggleParticipantInEvent(eventId, userPrincipal);
        });

        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_userNotFound_throwsException() {
        when(userService.getUserById(userId))
                .thenThrow(new UserNotFoundException(userId));

        assertThrows(UserNotFoundException.class, () -> {
            participantService.toggleParticipantInEvent(eventId, userPrincipal);
        });

        verify(userService).getUserById(userId);
        verify(eventService, never()).getEventById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_nullUserPrincipal_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            participantService.toggleParticipantInEvent(eventId, null);
        });

        verify(userService, never()).getUserById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_nullEventId_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            participantService.toggleParticipantInEvent(null, userPrincipal);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_repositorySaveFails_throwsException() {
        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(eventRepository.save(event))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            participantService.toggleParticipantInEvent(eventId, userPrincipal);
        });

        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(eventRepository).save(event);
    }

    @Test
    void toggleParticipantInEvent_eventHasNullParticipants_handlesGracefully() {
        event.setParticipants(null);

        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);

        assertThrows(NullPointerException.class, () -> {
            participantService.toggleParticipantInEvent(eventId, userPrincipal);
        });

        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
    }

    @Test
    void toggleParticipantInEvent_withMultipleExistingParticipants_onlyRemovesTargetUser() {
        User otherUser1 = new User();
        otherUser1.setId(UUID.randomUUID());
        User otherUser2 = new User();
        otherUser2.setId(UUID.randomUUID());

        participants.add(otherUser1);
        participants.add(otherUser2);
        participants.add(user);

        when(userService.getUserById(userId)).thenReturn(user);
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        participantService.toggleParticipantInEvent(eventId, userPrincipal);

        assertFalse(participants.contains(user));
        assertEquals(2, participants.size());
        assertTrue(participants.contains(otherUser1));
        assertTrue(participants.contains(otherUser2));

        verify(userService).getUserById(userId);
        verify(eventService).getEventById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(any());
    }
}
