package at.msm.asobo.services.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.user.UserBasicDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.AccessControlService;
import at.msm.asobo.services.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventAdminServiceTest {

  @Mock private EventRepository eventRepository;

  @Mock private UserService userService;

  @Mock private AccessControlService accessControlService;

  @Mock private UserDTOUserMapper userDTOUserMapper;

  @Mock private EventDTOEventMapper eventDTOEventMapper;

  @InjectMocks private EventAdminService eventAdminService;

  private User userJohn;
  private User userJane;
  private User creator;
  private User admin;
  private Event event;
  private UserPrincipal userPrincipal;
  private UserPrincipal creatorPrincipal;
  private UserBasicDTO userJohnBasicDTO;
  private UserBasicDTO userJaneBasicDTO;
  private Set<UserPublicDTO> eventAdminDTOs;

  @BeforeEach
  void setUp() {
    userJohn =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("john")
            .buildUserEntity();

    userJane =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("jane")
            .buildUserEntity();

    creator =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("creator")
            .withPassword("password")
            .buildUserEntity();

    admin =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("admin")
            .withPassword("password")
            .buildUserEntity();

    event =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withCreator(creator)
            .withEventAdmins(new HashSet<>())
            .buildEventEntity();

    userPrincipal = new UserTestBuilder().fromUser(userJohn).buildUserPrincipal();

    creatorPrincipal = new UserTestBuilder().fromUser(creator).buildUserPrincipal();

    eventAdminDTOs = new HashSet<>();
    UserPublicDTO userJohnDto = new UserTestBuilder().fromUser(userJohn).buildUserPublicDTO();

    eventAdminDTOs.add(userJohnDto);

    userJohnBasicDTO = new UserTestBuilder().fromUser(userJohn).buildUserBasicDTO();
    userJaneBasicDTO = new UserTestBuilder().fromUser(userJane).buildUserBasicDTO();
  }

  @Test
  void getAllEventAdminsByEventId_existingEvent_returnsAdminDTOs() {
    Set<User> eventAdmins = event.getEventAdmins();
    eventAdmins.add(admin);

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userDTOUserMapper.mapUsersToUserPublicDTOs(eventAdmins)).thenReturn(eventAdminDTOs);

    Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(event.getId());

    assertNotNull(result);
    assertEquals(eventAdminDTOs, result);
    verify(eventRepository).findById(event.getId());
    verify(userDTOUserMapper).mapUsersToUserPublicDTOs(eventAdmins);
  }

  @Test
  void getAllEventAdminsByEventId_eventNotFound_throwsException() {
    when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

    assertThrows(
        EventNotFoundException.class,
        () -> {
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

    assertTrue(result.isEmpty());
    verify(eventRepository).findById(event.getId());
    verify(userDTOUserMapper).mapUsersToUserPublicDTOs(event.getEventAdmins());
  }

  @Test
  void setAdminsToEvent_userIsEventCreator_addsAdmins() {
    User loggedInUser = creator; // User is the creator

    Set<UUID> userIdsToAdd = Set.of(userJohn.getId());
    Set<User> usersToAdd = Set.of(userJohn);
    Set<UserBasicDTO> userBasicDTOs = Set.of(userJohnBasicDTO);

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userService.getUserById(creator.getId())).thenReturn(loggedInUser);
    when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
    when(eventRepository.save(event)).thenReturn(event);
    when(userDTOUserMapper.mapUsersToUserBasicDTOs(event.getEventAdmins()))
        .thenReturn(userBasicDTOs);

    Set<UserBasicDTO> result =
        eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, creatorPrincipal);

    assertTrue(result.contains(userJohnBasicDTO));
    assertTrue(event.getEventAdmins().contains(userJohn));

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(creator.getId());
    verify(userService).getUsersByIds(userIdsToAdd);
    verify(eventRepository).save(event);
    verify(userDTOUserMapper).mapUsersToUserBasicDTOs(event.getEventAdmins());
  }

  @Test
  void setAdminsToEvent_userIsAdmin_addsAdmins() {
    Set<UUID> userIdsToAdd = Set.of(userJohn.getId(), userJane.getId());
    Set<User> usersToAdd = Set.of(userJohn, userJane);
    Set<UserBasicDTO> userBasicDTOs = Set.of(userJohnBasicDTO, userJaneBasicDTO);

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userService.getUserById(admin.getId())).thenReturn(admin);
    when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
    when(accessControlService.hasAdminRole(admin.getId())).thenReturn(true);
    when(eventRepository.save(event)).thenReturn(event);
    when(userDTOUserMapper.mapUsersToUserBasicDTOs(event.getEventAdmins()))
        .thenReturn(userBasicDTOs);

    UserPrincipal adminPrincipal = new UserTestBuilder().fromUser(admin).buildUserPrincipal();

    Set<UserBasicDTO> result =
        eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, adminPrincipal);

    assertTrue(result.containsAll(userBasicDTOs));
    assertTrue(event.getEventAdmins().containsAll(Set.of(userJohn, userJane)));
    assertEquals(2, event.getEventAdmins().size());

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(admin.getId());
    verify(userService).getUsersByIds(userIdsToAdd);
    verify(accessControlService).hasAdminRole(admin.getId());
    verify(eventRepository).save(event);
    verify(userDTOUserMapper).mapUsersToUserBasicDTOs(event.getEventAdmins());
  }

  @Test
  void setAdminsToEvent_userIsEventAdmin_addsAdmins() {
    event.getEventAdmins().add(userJohn); // makes sure that userJohn is an event admin

    Set<UUID> userIdsToAdd = Set.of(userJane.getId());
    Set<User> usersToAdd = Set.of(userJane);
    Set<UserBasicDTO> userBasicDTOs = Set.of(userJohnBasicDTO, userJaneBasicDTO);

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
    when(userService.getUsersByIds(userIdsToAdd)).thenReturn(usersToAdd);
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(false);
    when(eventRepository.save(event)).thenReturn(event);
    when(userDTOUserMapper.mapUsersToUserBasicDTOs(event.getEventAdmins()))
        .thenReturn(userBasicDTOs);

    Set<UserBasicDTO> result =
        eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, userPrincipal);

    assertTrue(result.containsAll(userBasicDTOs));
    assertTrue(event.getEventAdmins().contains(userJohn));
    assertTrue(event.getEventAdmins().contains(userJane));
    assertEquals(2, event.getEventAdmins().size());

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(userJohn.getId());
    verify(userService).getUsersByIds(userIdsToAdd);
    verify(accessControlService).hasAdminRole(userJohn.getId());
    verify(eventRepository).save(event);
    verify(userDTOUserMapper).mapUsersToUserBasicDTOs(event.getEventAdmins());
  }

  @Test
  void addAdminsToEvent_userNotAuthorized_throwsException() {
    Set<UUID> userIdsToAdd = Set.of(userJohn.getId(), userJane.getId());

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(false);

    assertThrows(
        UserNotAuthorizedException.class,
        () -> {
          eventAdminService.addAdminsToEvent(event.getId(), userIdsToAdd, userPrincipal);
        });

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(userJohn.getId());
    verify(accessControlService).hasAdminRole(userJohn.getId());
    verify(eventRepository, never()).save(any());
    verify(userDTOUserMapper, never()).mapUsersToUserBasicDTOs(any());
  }

  @Test
  void addAdminsToEvent_eventNotFound_throwsException() {
    Set<UUID> userIdsToAdd = Set.of(userJohn.getId());
    when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

    assertThrows(
        EventNotFoundException.class,
        () -> {
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
    when(accessControlService.hasAdminRole(creator.getId())).thenReturn(false);
    when(eventRepository.save(event)).thenReturn(event);
    when(userDTOUserMapper.mapUsersToUserBasicDTOs(event.getEventAdmins())).thenReturn(Set.of());

    Set<UserBasicDTO> result =
        eventAdminService.removeAdminsFromEvent(event.getId(), userIdsToRemove, creatorPrincipal);

    assertFalse(result.contains(userJohnBasicDTO));
    assertFalse(event.getEventAdmins().contains(userJohn));
    assertEquals(0, event.getEventAdmins().size());

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(creator.getId());
    verify(userService).getUsersByIds(userIdsToRemove);
    verify(accessControlService).hasAdminRole(creator.getId());
    verify(eventRepository).save(event);
    verify(userDTOUserMapper).mapUsersToUserBasicDTOs(event.getEventAdmins());
  }

  @Test
  void removeAdminsFromEvent_userNotAuthorized_throwsException() {
    Set<UUID> userIdsToRemove = Set.of(userJane.getId());
    Set<User> usersToRemove = Set.of(userJane);

    when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
    when(userService.getUserById(userJohn.getId())).thenReturn(userJohn);
    when(userService.getUsersByIds(userIdsToRemove)).thenReturn(usersToRemove);
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(false);

    assertThrows(
        UserNotAuthorizedException.class,
        () -> {
          eventAdminService.removeAdminsFromEvent(event.getId(), userIdsToRemove, userPrincipal);
        });

    verify(eventRepository).findById(event.getId());
    verify(userService).getUserById(userJohn.getId());
    verify(userService).getUsersByIds(userIdsToRemove);
    verify(accessControlService).hasAdminRole(userJohn.getId());
    verify(eventRepository, never()).save(any());
  }

  @Test
  void canManageEvent_userIsGlobalAdmin_returnsTrue() {
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(true);

    boolean result = eventAdminService.canManageEvent(event, userJohn.getId());

    assertTrue(result);
    verify(accessControlService).hasAdminRole(userJohn.getId());
  }

  @Test
  void canManageEvent_userIsEventCreator_returnsTrue() {
    when(accessControlService.hasAdminRole(creator.getId())).thenReturn(false);

    boolean result = eventAdminService.canManageEvent(event, creator.getId());

    assertTrue(result);
    verify(accessControlService).hasAdminRole(creator.getId());
  }

  @Test
  void canManageEvent_userIsEventAdmin_returnsTrue() {
    event.getEventAdmins().add(userJohn);
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(false);

    boolean result = eventAdminService.canManageEvent(event, userJohn.getId());

    assertTrue(result);
    verify(accessControlService).hasAdminRole(userJohn.getId());
  }

  @Test
  void canManageEvent_userIsNotAuthorized_returnsFalse() {
    when(accessControlService.hasAdminRole(userJohn.getId())).thenReturn(false);

    boolean result = eventAdminService.canManageEvent(event, userJohn.getId());

    assertFalse(result);
    verify(accessControlService).hasAdminRole(userJohn.getId());
  }
}
