package at.msm.asobo.services;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Role;
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
    private UUID userJohnId;
    private UUID userJaneId;
    private UUID creatorId;
    private UUID adminId;
    private User userJohn;
    private User userJane;
    private User creator;
    private User admin;
    private Event event;
    private UserPrincipal userPrincipal;
    private UserPrincipal creatorPrincipal;
    private Set<User> eventAdmins;
    private Set<UserPublicDTO> eventAdminDTOs;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userJohnId = UUID.randomUUID();
        userJaneId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        userJohn = new User();
        userJohn.setId(userJohnId);
        userJohn.setEmail("john@example.com");
        userJohn.setFirstName("John");
        userJohn.setSurname("Doe");

        userJane = new User();
        userJane.setId(userJaneId);
        userJane.setEmail("jane@example.com");
        userJane.setFirstName("Jane");
        userJane.setSurname("Doe");

        creator = new User();
        creator.setId(creatorId);
        creator.setEmail("creator@example.com");
        creator.setUsername("creater");
        creator.setPassword("password");

        admin = new User();
        admin.setId(adminId);
        admin.setEmail("admin@example.com");
        admin.setUsername("admin");
        admin.setPassword("password");

        event = new Event();
        event.setId(eventId);
        event.setCreator(creator);
        eventAdmins = new HashSet<>();
        event.setEventAdmins(eventAdmins);

        userPrincipal = new UserPrincipal(userJohnId, userJohn.getUsername(), userJohn.getPassword(), null);
        creatorPrincipal = new UserPrincipal(creatorId, creator.getUsername(), creator.getPassword(), null);

        eventAdminDTOs = new HashSet<>();
        UserPublicDTO userJohnDto = new UserPublicDTO();
        userJohnDto.setId(userJohnId);
        userJohnDto.setEmail("john@example.com");
        eventAdminDTOs.add(userJohnDto);

        eventDTO = new EventDTO();
        eventDTO.setId(eventId);
    }

    @Test
    void getAllEventAdminsByEventId_existingEvent_returnsAdminDTOs() {
        eventAdmins.add(admin);

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
        verify(eventRepository).findById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(eventAdmins);
    }

    @Test
    void addAdminsToEvent_userIsEventCreator_addsAdmins() {
        User loggedInUser = creator; // User is the creator

        Set<UUID> userIdsToAdd = Set.of(userJohnId);
        Set<User> usersToAdd = Set.of(userJohn);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(creatorId)).thenReturn(loggedInUser);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, creatorPrincipal);

        assertTrue(eventAdmins.contains(userJohn));
        assertNotNull(result);
        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(creatorId);
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userIsAdmin_addsAdmins() {
        Set<UUID> userIdsToAdd = Set.of(userJohnId, userJaneId);
        Set<User> usersToAdd = Set.of(userJohn, userJane);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(adminId)).thenReturn(admin);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(admin)).thenReturn(true);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        UserPrincipal adminPrincipal = new UserPrincipal(adminId, admin.getUsername(), admin.getPassword(), null);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, adminPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertTrue(eventAdmins.containsAll(Set.of(userJohn, userJane)));
        assertEquals(2, eventAdmins.size());

        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(adminId);
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(admin);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userIsEventAdmin_addsAdmins() {
        eventAdmins.add(userJohn); // User is already an event admin

        Set<UUID> userIdsToAdd = Set.of(userJaneId);
        Set<User> usersToAdd = Set.of(userJane);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohnId)).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertTrue(eventAdmins.contains(userJohn));
        assertTrue(eventAdmins.contains(userJane));
        assertEquals(2, eventAdmins.size());

        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(userJohnId);
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(userJohn);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(userJohnId, userJaneId);
        Set<User> usersToAdd = Set.of(userJohn, userJane);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohnId)).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);
        });

        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(userJohnId);
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(userJohn);
        verify(eventRepository, never()).save(any());
        verify(eventDTOEventMapper, never()).mapEventToEventDTO(any());
    }

    @Test
    void addAdminsToEvent_eventNotFound_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(userJohnId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventAdminService.addAdminsToEvent(eventId, userIdsToAdd, userPrincipal);
        });

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void removeAdminsFromEvent_userIsEventCreator_removesAdmins() {
        eventAdmins.add(userJohn);

        Set<UUID> userIdsToRemove = Set.of(userJohnId);
        Set<User> usersToRemove = Set.of(userJohn);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(creatorId)).thenReturn(creator);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(accessControlService.hasAdminRole(creator)).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.removeAdminsFromEvent(eventId, userIdsToRemove, creatorPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertFalse(eventAdmins.contains(userJohn));
        assertEquals(0, eventAdmins.size());

        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(creatorId);
        verify(userService).getUsersByIds(userIdsToRemove);
        verify(accessControlService).hasAdminRole(creator);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void removeAdminsFromEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToRemove = Set.of(userJaneId);
        Set<User> usersToRemove = Set.of(userJane);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohnId)).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.removeAdminsFromEvent(eventId, userIdsToRemove, userPrincipal);
        });

        verify(eventRepository).findById(eventId);
        verify(userService).getUserById(userJohnId);
        verify(userService).getUsersByIds(userIdsToRemove);
        verify(eventRepository, never()).save(any());
        verify(eventDTOEventMapper, never()).mapEventToEventDTO(any());
    }

    @Test
    void canManageEvent_userIsGlobalAdmin_returnsTrue() {
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(true);

        boolean result = eventAdminService.canManageEvent(event, userJohn);

        assertTrue(result);
        verify(accessControlService).hasAdminRole(userJohn);
    }

    @Test
    void canManageEvent_userIsEventCreator_returnsTrue() {
        when(accessControlService.hasAdminRole(creator)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, creator);

        assertTrue(result);
        verify(accessControlService).hasAdminRole(creator);
    }

    @Test
    void canManageEvent_userIsEventAdmin_returnsTrue() {
        eventAdmins.add(userJohn);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, userJohn);

        assertTrue(result);
        verify(accessControlService).hasAdminRole(userJohn);
    }

    @Test
    void canManageEvent_userIsNotAuthorized_returnsFalse() {
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        boolean result = eventAdminService.canManageEvent(event, userJohn);

        assertFalse(result);
        verify(accessControlService).hasAdminRole(userJohn);
    }
}