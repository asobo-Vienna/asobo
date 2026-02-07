package at.msm.asobo.services;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.builders.UserCommentTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommentServiceTest {

    @Mock
    UserCommentRepository userCommentRepository;

    @Mock
    EventService eventService;

    @Mock
    UserService userService;

    @Mock
    AccessControlService accessControlService;

    @Mock
    UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;

    @InjectMocks
    UserCommentService userCommentService;

    private LocalDateTime dateTimeNow;

    private UserComment userComment1;
    private UserComment userComment2;

    private UserCommentDTO userCommentDTO1;
    private UserCommentDTO userCommentDTO2;

    private List<UserComment> userComments;
    private List<UserCommentDTO> userCommentDTOs;

    private final List<UserComment> userCommentsEmpty =  new ArrayList<>();
    private final List<UserCommentDTO> userCommentDTOsEmpty = new ArrayList<>();

    private User author;
    private User notAuthor;

    private UserPrincipal loggedInAuthor;
    private UserPrincipal loggedInNotAuthor;

    private Event event;

    @BeforeEach
    void setUp() {

        dateTimeNow = LocalDateTime.now();

        author = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("TestAuthor")
                .buildUserEntity();

        loggedInAuthor = new UserTestBuilder()
                .fromUser(author)
                .buildUserPrincipal();

        notAuthor = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("NotTestAuthor")
                .buildUserEntity();

        loggedInNotAuthor = new UserTestBuilder()
                .fromUser(notAuthor)
                .buildUserPrincipal();

        event = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withCreator(author)
                .withEventAdmins(new HashSet<>())
                .buildEventEntity();

        userComment1 = new UserCommentTestBuilder()
                .withId(UUID.randomUUID())
                .withText("Test comment #1")
                .withAuthor(author)
                .withEvent(event)
                .buildUserComment();

        userComment2 = new UserCommentTestBuilder()
                .withId(UUID.randomUUID())
                .withText("Test comment #2")
                .withAuthor(author)
                .withEvent(event)
                .buildUserComment();

        userCommentDTO1 = new UserCommentTestBuilder()
                .fromUserComment(userComment1)
                .buildUserCommentDTO();

        userCommentDTO2 = new UserCommentTestBuilder()
                .fromUserComment(userComment2)
                .buildUserCommentDTO();

        userComments = List.of(userComment1, userComment2);
        userCommentDTOs = List.of(userCommentDTO1, userCommentDTO2);
    }

    @Test
    void getUserCommentDTOById_returnsComment() {
        when(userCommentRepository.findById(userComment1.getId())).thenReturn(Optional.of(userComment1));
        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1))
                .thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService.getUserCommentDTOById(userComment1.getId());

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findById(userComment1.getId());
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void getUserCommentDTOById_throwsExceptionIfNotFound() {
        when(userCommentRepository.findById(userComment1.getId())).thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentDTOById(userComment1.getId()));

        verify(userCommentRepository).findById(userComment1.getId());
    }

    @Test
    void getUserCommentById_returnsComment() {

        when(userCommentRepository.findById(userComment1.getId())).thenReturn(Optional.of(userComment1));

        UserComment result = userCommentService.getUserCommentById(userComment1.getId());

        assertNotNull(result);
        assertEquals(result, userComment1);

        verify(userCommentRepository).findById(userComment1.getId());
    }

    @Test
    void getUserCommentById_throwsExceptionIfNotFound() {
        when(userCommentRepository.findById(userComment1.getId())).thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentById(userComment1.getId()));

        verify(userCommentRepository).findById(userComment1.getId());
    }

    @Test
    void getUserCommentsByCreationDate_returnsSingleCommentList() {
        List<UserComment> singleCommentList = List.of(userComment1);
        List<UserCommentDTO> singleCommentDTOList = List.of(userCommentDTO1);

        when(userCommentRepository.findUserCommentsByCreationDate(dateTimeNow))
                .thenReturn(singleCommentList);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(singleCommentList))
                .thenReturn(singleCommentDTOList);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByCreationDate(dateTimeNow);

        assertEquals(1, result.size());
        assertEquals(result, singleCommentDTOList);

        verify(userCommentRepository).findUserCommentsByCreationDate(dateTimeNow);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(singleCommentList);
    }

    @Test
    void getUserCommentsByCreationDate_returnsComments() {
        when(userCommentRepository.findUserCommentsByCreationDate(dateTimeNow))
                .thenReturn(userComments);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments))
                .thenReturn(userCommentDTOs);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByCreationDate(dateTimeNow);

        assertNotNull(result);
        assertEquals(result, userCommentDTOs);
        assertEquals(2, result.size());

        verify(userCommentRepository).findUserCommentsByCreationDate(dateTimeNow);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userComments);
    }

    @Test
    void getUserCommentsByCreationDate_returnsEmptyListIfNoComments() {
        when(userCommentRepository.findUserCommentsByCreationDate(dateTimeNow))
                .thenReturn(userCommentsEmpty);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userCommentsEmpty))
                .thenReturn(userCommentDTOsEmpty);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByCreationDate(dateTimeNow);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(result, userCommentDTOsEmpty);

        verify(userCommentRepository).findUserCommentsByCreationDate(dateTimeNow);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByAuthor_returnsComments() {
        when(userCommentRepository.findUserCommentsByAuthor(author))
                .thenReturn(userComments);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments))
                .thenReturn(userCommentDTOs);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByAuthor(author);

        assertNotNull(result);
        assertEquals(result, userCommentDTOs);
        assertEquals(2, result.size());

        verify(userCommentRepository).findUserCommentsByAuthor(author);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userComments);
    }

    @Test
    void getUserCommentsByAuthor_returnsEmptyListIfNoComments() {
        when(userCommentRepository.findUserCommentsByAuthor(author))
                .thenReturn(userCommentsEmpty);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userCommentsEmpty))
                .thenReturn(userCommentDTOsEmpty);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByAuthor(author);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(result, userCommentDTOsEmpty);

        verify(userCommentRepository).findUserCommentsByAuthor(author);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByEvent_returnsComments() {
        when(userCommentRepository.findUserCommentsByEvent(event))
                .thenReturn(userComments);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments))
                .thenReturn(userCommentDTOs);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEvent(event);

        assertNotNull(result);
        assertEquals(result, userCommentDTOs);
        assertEquals(2, result.size());

        verify(userCommentRepository).findUserCommentsByEvent(event);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userComments);
    }

    @Test
    void getUserCommentsByEvent_returnsEmptyListIfNoComments() {
        when(userCommentRepository.findUserCommentsByEvent(event))
                .thenReturn(userCommentsEmpty);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userCommentsEmpty))
                .thenReturn(userCommentDTOsEmpty);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEvent(event);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(result, userCommentDTOsEmpty);

        verify(userCommentRepository).findUserCommentsByEvent(event);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByEventId_returnsComments() {
        when(userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(event.getId()))
                .thenReturn(userComments);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments))
                .thenReturn(userCommentDTOs);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEventId(event.getId());

        assertNotNull(result);
        assertEquals(result, userCommentDTOs);
        assertEquals(2, result.size());

        verify(userCommentRepository).findUserCommentsByEventIdOrderByCreationDate(event.getId());
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userComments);
    }

    @Test
    void getUserCommentsByEventId_returnsEmptyListIfNoComments() {

        when(userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(event.getId()))
                .thenReturn(userCommentsEmpty);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userCommentsEmpty))
                .thenReturn(userCommentDTOsEmpty);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEventId(event.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(result, userCommentDTOsEmpty);

        verify(userCommentRepository).findUserCommentsByEventIdOrderByCreationDate(event.getId());
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentByEventIdAndCommentId_returnsComment() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.of(userComment1));
        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1))
                .thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService.getUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId());

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void getUserCommentByEventIdAndCommentId_throwsExceptionIfNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment2.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentByEventIdAndCommentId(event.getId(), userComment2.getId()));

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment2.getId());
    }

    @Test
    void addNewUserCommentToEventById_addsNewComment() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userService.getUserById(author.getId())).thenReturn(author);
        when(userCommentDTOUserCommentMapper
                .mapUserCommentDTOToUserComment(userCommentDTO2, author, event)).thenReturn(userComment2);
        when(userCommentRepository.save(userComment2)).thenReturn(userComment2);
        when(userCommentDTOUserCommentMapper
                .mapUserCommentToUserCommentDTO(userComment2)).thenReturn(userCommentDTO2);

        UserCommentDTO result = userCommentService.addNewUserCommentToEventById(event.getId(), userCommentDTO2);

        assertNotNull(result);
        assertEquals(result, userCommentDTO2);

        verify(eventService).getEventById(event.getId());
        verify(userService).getUserById(userCommentDTO2.getAuthorId());
        verify(userCommentRepository).save(userComment2);
        verify(userCommentDTOUserCommentMapper).mapUserCommentDTOToUserComment(userCommentDTO2, author, event);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment2);
    }

    @Test
    void addNewUserCommentToEventById_throwsExceptionIfEventNotFound() {
        when(eventService.getEventById(event.getId()))
                .thenThrow(new EventNotFoundException(event.getId()));

        assertThrows(EventNotFoundException.class, () -> userCommentService.addNewUserCommentToEventById(event.getId(), userCommentDTO1));

        verify(eventService).getEventById(event.getId());
        verify(userService, never()).getUserById(any());
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void addNewUserCommentToEventById_throwsExceptionIfAuthorNotFound() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userService.getUserById(userCommentDTO1.getAuthorId()))
                .thenThrow(new UserNotFoundException(userCommentDTO1.getAuthorId()));

        assertThrows(UserNotFoundException.class, () -> userCommentService.addNewUserCommentToEventById(event.getId(), userCommentDTO1));

        verify(eventService).getEventById(event.getId());
        verify(userService).getUserById(userCommentDTO1.getAuthorId());
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void updateUserCommentByEventIdAndCommentId_updatesComment() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId())).thenReturn(Optional.of(userComment1));

        when(userService.getUserById(author.getId())).thenReturn(author);

        when(userCommentRepository.save(userComment1)).thenReturn(userComment1);

        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1)).thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService
                .updateUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), userCommentDTO1, loggedInAuthor);

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(accessControlService).assertCanUpdateComment(userComment1, author);
        verify(userService).getUserById(userCommentDTO1.getAuthorId());
        verify(userCommentRepository).save(userComment1);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void updateUserCommentByEventIdAndCommentId_throwsExceptionIfUserIsNotAuthor() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.of(userComment1));

        when(userService.getUserById(notAuthor.getId())).thenReturn(notAuthor);

        doThrow(new UserNotAuthorizedException("You are not allowed to update this comment"))
                .when(accessControlService).assertCanUpdateComment(userComment1, notAuthor);

        assertThrows(UserNotAuthorizedException.class, () ->
                userCommentService
                        .updateUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), userCommentDTO1, loggedInNotAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService).getUserById(notAuthor.getId());
        verify(accessControlService).assertCanUpdateComment(userComment1, notAuthor);
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void updateUserCommentByEventIdAndCommentId_throwsExceptionIfCommentNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () ->
                userCommentService.updateUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), userCommentDTO1, loggedInAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService, never()).getUserById(any());
        verify(accessControlService, never()).assertCanUpdateComment(any(), any());
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void updateUserCommentByEventIdAndCommentId_throwsExceptionIfAuthorNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.of(userComment1));

        when(userService.getUserById(author.getId()))
                .thenThrow(new UserNotFoundException(author.getId()));

        assertThrows(UserNotFoundException.class, () ->
                userCommentService
                        .updateUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), userCommentDTO1, loggedInAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService).getUserById(author.getId());
        verify(accessControlService, never()).assertCanUpdateComment(any(), any());
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void deleteUserCommentByEventIdAndCommentId_deletesComment() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId())).thenReturn(Optional.of(userComment1));

        when(userService.getUserById(author.getId())).thenReturn(author);

        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1)).thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService
                .deleteUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), loggedInAuthor);

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService).getUserById(userCommentDTO1.getAuthorId());
        verify(accessControlService).assertCanDeleteComment(userComment1, author);
        verify(userCommentRepository).delete(userComment1);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void deleteUserCommentByEventIdAndCommentId_throwsExceptionIfUserIsNotAuthor() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.of(userComment1));

        when(userService.getUserById(notAuthor.getId())).thenReturn(notAuthor);

        doThrow(new UserNotAuthorizedException("You are not allowed to delete this comment"))
                .when(accessControlService).assertCanDeleteComment(userComment1, notAuthor);

        assertThrows(UserNotAuthorizedException.class, () ->
                userCommentService
                        .deleteUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), loggedInNotAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(accessControlService).assertCanDeleteComment(userComment1, notAuthor);
        verify(userCommentRepository, never()).delete(any());
    }

    @Test
    void deleteUserCommentByEventIdAndCommentId_throwsExceptionIfCommentNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () ->
                userCommentService.deleteUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), loggedInAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService, never()).getUserById(any());
        verify(accessControlService, never()).assertCanDeleteComment(any(), any());
        verify(userCommentRepository, never()).delete(any());
    }

    @Test
    void deleteUserCommentByEventIdAndCommentId_throwsExceptionIfAuthorNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(event.getId(), userComment1.getId()))
                .thenReturn(Optional.of(userComment1));

        when(userService.getUserById(author.getId()))
                .thenThrow(new UserNotFoundException(author.getId()));

        assertThrows(UserNotFoundException.class, () ->
                userCommentService
                        .deleteUserCommentByEventIdAndCommentId(event.getId(), userComment1.getId(), loggedInAuthor)
        );

        verify(userCommentRepository).findUserCommentByEventIdAndId(event.getId(), userComment1.getId());
        verify(userService).getUserById(author.getId());
        verify(accessControlService, never()).assertCanDeleteComment(any(), any());
        verify(userCommentRepository, never()).delete(any());
    }
}