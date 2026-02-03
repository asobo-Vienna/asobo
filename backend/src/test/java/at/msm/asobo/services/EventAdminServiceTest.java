package at.msm.asobo.services;

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
import at.msm.asobo.services.events.EventAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventAdminServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @Mock
    private AccessControlService accessControlService;

    @Mock
    private UserDTOUserMapper userDTOUserMapper;

    @Mock
    private EventDTOEventMapper eventDTOEventMapper;

    @InjectMocks
    private EventAdminService eventAdminService;

    private UUID eventId;
    private UUID userId;
    private UUID creatorId;
    private User user;
    private User creator;
    private Event event;
    private UserPrincipal userPrincipal;
    private Set<User> eventAdmins;
    private Set<UserPublicDTO> eventAdminDTOs;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        creatorId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setSurname("User");

        creator = new User();
        creator.setId(creatorId);
        creator.setEmail("creator@example.com");

        event = new Event();
        event.setId(eventId);
        event.setCreator(creator);
        eventAdmins = new HashSet<>();
        event.setEventAdmins(eventAdmins);

        userPrincipal = new UserPrincipal(userId, "test@example.com", "password", null);

        eventAdminDTOs = new HashSet<>();
        UserPublicDTO dto = new UserPublicDTO();
        dto.setId(userId);
        dto.setEmail("test@example.com");
        eventAdminDTOs.add(dto);

        eventDTO = new EventDTO();
        eventDTO.setId(eventId);
    }

    @Test
    void getAllEventAdminsByEventId_existingEvent_returnsAdminDTOs() {
        User admin1 = new User();
        admin1.setId(UUID.randomUUID());
        eventAdmins.add(admin1);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(eventAdmins))
                .thenReturn(eventAdminDTOs);

        Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(eventId);

        assertNotNull(result);
        assertEquals(eventAdminDTOs, result);
        verify(eventRepository).findById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(eventAdmins);
    }

    @Test
    void getAllEventAdminsByEventId_eventNotFound_throwsException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventAdminService.getAllEventAdminsByEventId(eventId);
        });

        verify(eventRepository).findById(eventId);
        verify(userDTOUserMapper, never()).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllEventAdminsByEventId_emptyAdminList_returnsEmptySet() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(eventAdmins))
                .thenReturn(new HashSet<>());

        Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(eventId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addAdminsToEvent_userIsEventCreator_addsAdmins() {
        User loggedInUser = creator; // User is the creator
        UserPrincipal creatorPrincipal = new UserPrincipal(creatorId, "creator@example.com", "password", null);

        Set<UUID> userIdsToAdd = Set.of(userId);
        Set<User> usersToAdd = Set.of(user);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(creatorId)).thenReturn(loggedInUser);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, creatorPrincipal);

        assertTrue(eventAdmins.contains(user));
        assertNotNull(result);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userIsAdmin_addsAdmins() {
        Set<UUID> userIdsToAdd = Set.of(UUID.randomUUID());
        Set<User> usersToAdd = Set.of(new User());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(user)).thenReturn(true);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void addAdminsToEvent_userIsEventAdmin_addsAdmins() {
        eventAdmins.add(user); // User is already an event admin
        Set<UUID> userIdsToAdd = Set.of(UUID.randomUUID());
        Set<User> usersToAdd = Set.of(new User());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(user)).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void addAdminsToEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(UUID.randomUUID());
        Set<User> usersToAdd = Set.of(new User());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(user)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    void addAdminsToEvent_eventNotFound_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(userId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    void removeAdminsFromEvent_userIsEventCreator_removesAdmins() {
        User adminToRemove = new User();
        adminToRemove.setId(UUID.randomUUID());
        eventAdmins.add(adminToRemove);

        UserPrincipal creatorPrincipal = new UserPrincipal(creatorId, "creator@example.com", "password", null);
        Set<UUID> userIdsToRemove = Set.of(adminToRemove.getId());
        Set<User> usersToRemove = Set.of(adminToRemove);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(creatorId)).thenReturn(creator);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.removeAdminsFromEvent(eventId, userIdsToRemove, creatorPrincipal);

        assertFalse(eventAdmins.contains(adminToRemove));
        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void removeAdminsFromEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToRemove = Set.of(UUID.randomUUID());
        Set<User> usersToRemove = Set.of(new User());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(accessControlService.hasAdminRole(user)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.removeAdminsFromEvent(eventId, userIdsToRemove, userPrincipal);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    void canManageEvent_userIsGlobalAdmin_returnsTrue() {
        when(accessControlService.hasAdminRole(user)).thenReturn(true);

        boolean result = eventAdminService.canManageEvent(event, user);

        assertTrue(result);
        verify(accessControlService).hasAdminRole(user);
    }

    @Test
    void canManageEvent_userIsEventCreator_returnsTrue() {
        when(accessControlService.hasAdminRole(creator)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, creator);

        assertTrue(result);
    }

    @Test
    void canManageEvent_userIsEventAdmin_returnsTrue() {
        eventAdmins.add(user);
        when(accessControlService.hasAdminRole(user)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, user);

        assertTrue(result);
    }

    @Test
    void canManageEvent_userIsNotAuthorized_returnsFalse() {
        when(accessControlService.hasAdminRole(user)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, user);

        assertFalse(result);
    }
}