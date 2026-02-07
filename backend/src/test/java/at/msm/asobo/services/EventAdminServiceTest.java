package at.msm.asobo.services;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
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

    private User userJohn;
    private User userJane;
    private User creator;
    private User admin;
    private Event event;
    private UserPrincipal userPrincipal;
    private UserPrincipal creatorPrincipal;
    private Set<UserPublicDTO> eventAdminDTOs;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        userJohn = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("john")
                .buildUserEntity();

        userJane = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("jane")
                .buildUserEntity();

        creator = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("creator")
                .withPassword("password")
                .buildUserEntity();

        admin = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("admin")
                .withPassword("password")
                .buildUserEntity();

        event = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withCreator(creator)
                .withEventAdmins(new HashSet<>())
                .buildEventEntity();

        userPrincipal = new UserTestBuilder()
                .fromUser(userJohn)
                .buildUserPrincipal();

        creatorPrincipal = new UserTestBuilder()
                .fromUser(creator)
                .buildUserPrincipal();

        eventAdminDTOs = new HashSet<>();
        UserPublicDTO userJohnDto = new UserTestBuilder()
                .fromUser(userJohn)
                .buildUserPublicDTO();

        eventAdminDTOs.add(userJohnDto);

        eventDTO = new EventTestBuilder()
                .fromEvent(event)
                .buildEventDTO();
    }

    @Test
    void getAllEventAdminsByEventId_existingEvent_returnsAdminDTOs() {
        Set<User> eventAdmins = event.getEventAdmins();
        eventAdmins.add(admin);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(eventAdmins))
                .thenReturn(eventAdminDTOs);

        Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(event.getId());

        assertNotNull(result);
        assertEquals(eventAdminDTOs, result);
        verify(eventRepository).findById(event.getId());
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(eventAdmins);
    }

    @Test
    void getAllEventAdminsByEventId_eventNotFound_throwsException() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventAdminService.getAllEventAdminsByEventId(event.getId());
        });

        verify(eventRepository).findById(event.getId());
        verify(userDTOUserMapper, never()).mapUsersToUserPublicDTOs(any());
    }

    @Test
    void getAllEventAdminsByEventId_emptyAdminList_returnsEmptySet() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(event.getEventAdmins()))
                .thenReturn(new HashSet<>());

        Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(event.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventRepository).findById(event.getId());
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getEventAdmins());
    }

    @Test
    void addAdminsToEvent_userIsEventCreator_addsAdmins() {
        User loggedInUser = creator; // User is the creator

        Set<UUID> userIdsToAdd = Set.of(userJohn.getId());
        Set<User> usersToAdd = Set.of(userJohn);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(creator.getId())).thenReturn(loggedInUser);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, creatorPrincipal);

        assertTrue(event.getEventAdmins().contains(userJohn));
        assertNotNull(result);
        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(creator.getId());
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userIsAdmin_addsAdmins() {
        Set<UUID> userIdsToAdd = Set.of(userJohn.getId(), userJane.getId());
        Set<User> usersToAdd = Set.of(userJohn, userJane);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(admin.getId())).thenReturn(admin);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(admin)).thenReturn(true);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        UserPrincipal adminPrincipal = new UserTestBuilder()
                .fromUser(admin)
                .buildUserPrincipal();

        EventDTO result = eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, adminPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertTrue(event.getEventAdmins().containsAll(Set.of(userJohn, userJane)));
        assertEquals(2, event.getEventAdmins().size());

        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(admin.getId());
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(admin);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userIsEventAdmin_addsAdmins() {
        event.getEventAdmins().add(userJohn); // makes sure that userJohn is an event admin

        Set<UUID> userIdsToAdd = Set.of(userJane.getId());
        Set<User> usersToAdd = Set.of(userJane);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, userPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertTrue(event.getEventAdmins().contains(userJohn));
        assertTrue(event.getEventAdmins().contains(userJane));
        assertEquals(2, event.getEventAdmins().size());

        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(userJohn.getId());
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(userJohn);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void addAdminsToEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(userJohn.getId(), userJane.getId());
        Set<User> usersToAdd = Set.of(userJohn, userJane);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, userPrincipal);
        });

        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(userJohn.getId());
        verify(userService).getUsersByIds(userIdsToAdd);
        verify(accessControlService).hasAdminRole(userJohn);
        verify(eventRepository, never()).save(any());
        verify(eventDTOEventMapper, never()).mapEventToEventDTO(any());
    }

    @Test
    void addAdminsToEvent_eventNotFound_throwsException() {
        Set<UUID> userIdsToAdd = Set.of(userJohn.getId());
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, userPrincipal);
        });

        verify(eventRepository).findById(event.getId());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void removeAdminsFromEvent_userIsEventCreator_removesAdmins() {
        event.getEventAdmins().add(userJohn);

        Set<UUID> userIdsToRemove = Set.of(userJohn.getId());
        Set<User> usersToRemove = Set.of(userJohn);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(creator.getId())).thenReturn(creator);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(accessControlService.hasAdminRole(creator)).thenReturn(false);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDTOEventMapper.mapEventToEventDTO(event)).thenReturn(eventDTO);

        EventDTO result = eventAdminService.removeAdminsFromEvent(event.getId(), userIdsToRemove, creatorPrincipal);

        assertNotNull(result);
        assertEquals(eventDTO, result);
        assertFalse(event.getEventAdmins().contains(userJohn));
        assertEquals(0, event.getEventAdmins().size());

        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(creator.getId());
        verify(userService).getUsersByIds(userIdsToRemove);
        verify(accessControlService).hasAdminRole(creator);
        verify(eventRepository).save(event);
        verify(eventDTOEventMapper).mapEventToEventDTO(event);
    }

    @Test
    void removeAdminsFromEvent_userNotAuthorized_throwsException() {
        Set<UUID> userIdsToRemove = Set.of(userJane.getId());
        Set<User> usersToRemove = Set.of(userJane);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
        when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
        when(accessControlService.hasAdminRole(userJohn)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () -> {
            eventAdminService.removeAdminsFromEvent(event.getId(), userIdsToRemove, userPrincipal);
        });

        verify(eventRepository).findById(event.getId());
        verify(userService).getUserById(userJohn.getId());
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
        event.getEventAdmins().add(userJohn);
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