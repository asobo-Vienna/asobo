package at.msm.asobo.services;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
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

    private Event event;
    private User participant1;
    private User participant2;
    private User participant3;
    private UserPrincipal userPrincipal;
    private Set<UserPublicDTO> participantDTOs;

    @BeforeEach
    void setUp() {
        participant1 = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("testuser1")
                .withFirstName("Test")
                .withSurname("User1")
                .buildUserEntity();

        participant2 = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("testuser2")
                .withFirstName("Test")
                .withSurname("User2")
                .buildUserEntity();

        participant3 = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("testuser3")
                .withFirstName("Test")
                .withSurname("User3")
                .buildUserEntity();

        event = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withParticipants(new HashSet<>())
                .buildEventEntity();

        userPrincipal = new UserTestBuilder()
                .fromUser(participant1)
                .buildUserPrincipal();

        participantDTOs = new HashSet<>();
        UserPublicDTO dto = new UserTestBuilder()
                .fromUser(participant1)
                .buildUserPublicDTO();
        participantDTOs.add(dto);
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_returnsParticipantDTOs() {
        event.getParticipants().add(participant1);
        event.getParticipants().add(participant2);

        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(event.getParticipants()))
                .thenReturn(participantDTOs);

        Set<UserPublicDTO> result = participantService.getAllParticipantsAsDTOsByEventId(event.getId());

        assertNotNull(result);
        assertEquals(participantDTOs, result);
        verify(eventService).getEventById(event.getId());
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getParticipants());
    }

    @Test
    void toggleParticipantInEvent_userNotParticipating_addsUser() {
        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        Set<UserPublicDTO> result = participantService.toggleParticipantInEvent(event.getId(), userPrincipal);

        assertTrue(event.getParticipants().contains(participant1));
        assertEquals(1, event.getParticipants().size());
        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getParticipants());
        assertNotNull(result);
    }

    @Test
    void toggleParticipantInEvent_userAlreadyParticipating_removesUser() {
        event.getParticipants().add(participant1); // make sure participant1 is already participating

        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(new HashSet<>());

        Set<UserPublicDTO> result = participantService.toggleParticipantInEvent(event.getId(), userPrincipal);

        assertFalse(event.getParticipants().contains(participant1));
        assertEquals(0, event.getParticipants().size());
        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getParticipants());
        assertNotNull(result);
    }

    @Test
    void toggleParticipantInEvent_multipleParticipants_onlyTogglesLoggedInUser() {
        event.getParticipants().add(participant2);

        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        participantService.toggleParticipantInEvent(event.getId(), userPrincipal);

        assertEquals(2, event.getParticipants().size());
        assertTrue(event.getParticipants().contains(participant1));
        assertTrue(event.getParticipants().contains(participant2));
        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(eventRepository).save(event);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void toggleParticipantInEvent_eventNotFound_throwsException() {
        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId()))
                .thenThrow(new EventNotFoundException(event.getId()));

        assertThrows(EventNotFoundException.class, () -> {
            participantService.toggleParticipantInEvent(event.getId(), userPrincipal);
        });

        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_userNotFound_throwsException() {
        when(userService.getUserById(participant1.getId()))
                .thenThrow(new UserNotFoundException(participant1.getId()));

        assertThrows(UserNotFoundException.class, () -> {
            participantService.toggleParticipantInEvent(event.getId(), userPrincipal);
        });

        verify(userService).getUserById(participant1.getId());
        verify(eventService, never()).getEventById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void toggleParticipantInEvent_nullUserPrincipal_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            participantService.toggleParticipantInEvent(event.getId(), null);
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
        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(eventRepository.save(event))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            participantService.toggleParticipantInEvent(event.getId(), userPrincipal);
        });

        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(eventRepository).save(event);
    }

    @Test
    void toggleParticipantInEvent_eventHasNullParticipants_handlesGracefully() {
        event.setParticipants(null);

        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);

        assertThrows(NullPointerException.class, () -> {
            participantService.toggleParticipantInEvent(event.getId(), userPrincipal);
        });

        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
    }

    @Test
    void toggleParticipantInEvent_withMultipleExistingParticipants_onlyRemovesTargetUser() {
        event.getParticipants().add(participant1);
        event.getParticipants().add(participant2);
        event.getParticipants().add(participant3);

        when(userService.getUserById(participant1.getId())).thenReturn(participant1);
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(any())).thenReturn(participantDTOs);

        participantService.toggleParticipantInEvent(event.getId(), userPrincipal);

        assertFalse(event.getParticipants().contains(participant1));
        assertEquals(2, event.getParticipants().size());
        assertTrue(event.getParticipants().contains(participant2));
        assertTrue(event.getParticipants().contains(participant3));

        verify(userService).getUserById(participant1.getId());
        verify(eventService).getEventById(event.getId());
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_eventNotFound_throwsException() {
        when(eventService.getEventById(event.getId()))
                .thenThrow(new EventNotFoundException(event.getId()));

        assertThrows(EventNotFoundException.class, () -> {
            participantService.getAllParticipantsAsDTOsByEventId(event.getId());
        });

        verify(eventService).getEventById(event.getId());
        verify(userDTOUserMapper, never()).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllParticipantsAsDTOsByEventId_emptyParticipantsList_returnsEmptySet() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(event.getParticipants()))
                .thenReturn(new HashSet<>());

        Set<UserPublicDTO> result = participantService.getAllParticipantsAsDTOsByEventId(event.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventService).getEventById(event.getId());
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getParticipants());
    }
}
