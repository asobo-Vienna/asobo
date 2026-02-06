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
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;
import at.msm.asobo.repositories.UserCommentRepository;
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

    private UUID commentId1;
    private UUID commentId2;

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
    private Event event;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        commentId1 = UUID.randomUUID();
        commentId2 = UUID.randomUUID();

        dateTimeNow = LocalDateTime.now();

        author = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("TestAuthor")
                .buildUserEntity();

        eventId = UUID.randomUUID();

        event = new EventTestBuilder()
                .withId(eventId)
                .withCreator(author)
                .withEventAdmins(new HashSet<>())
                .buildEventEntity();

        userComment1 = new UserCommentTestBuilder()
                .withId(commentId1)
                .withText("Test comment #1")
                .withAuthor(author)
                .withEvent(event)
                .buildUserComment();

        userComment2 = new UserCommentTestBuilder()
                .withId(commentId2)
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
    void getUserCommentDTOById_returnsCommentIfExists() {

        when(userCommentRepository.findById(commentId1)).thenReturn(Optional.of(userComment1));
        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1))
                .thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService.getUserCommentDTOById(commentId1);

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findById(commentId1);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void getUserCommentDTOById_throwsExceptionIfNotFound() {
        when(userCommentRepository.findById(commentId1)).thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentDTOById(commentId1));

        verify(userCommentRepository).findById(commentId1);
        verify(userCommentDTOUserCommentMapper, never()).mapUserCommentToUserCommentDTO(any());
    }

    @Test
    void getUserCommentById_returnsCommentIfExists() {

        when(userCommentRepository.findById(commentId1)).thenReturn(Optional.of(userComment1));

        UserComment result = userCommentService.getUserCommentById(commentId1);

        assertNotNull(result);
        assertEquals(result, userComment1);

        verify(userCommentRepository).findById(commentId1);
    }

    @Test
    void getUserCommentById_throwsExceptionIfNotFound() {
        when(userCommentRepository.findById(commentId1)).thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentById(commentId1));

        verify(userCommentRepository).findById(commentId1);
    }

    @Test
    void getUserCommentsByCreationDate_returnsSingleCommentIfExists() {
        List<UserComment> singleComment = List.of(userComment1);
        List<UserCommentDTO> singleCommentDTO = List.of(userCommentDTO1);

        when(userCommentRepository.findUserCommentsByCreationDate(dateTimeNow))
                .thenReturn(singleComment);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(singleComment))
                .thenReturn(singleCommentDTO);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByCreationDate(dateTimeNow);

        assertEquals(1, result.size());
        assertEquals(result, singleCommentDTO);

        verify(userCommentRepository).findUserCommentsByCreationDate(dateTimeNow);
    }

    @Test
    void getUserCommentsByCreationDate_returnsCommentsIfExist() {
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

        verify(userCommentRepository).findUserCommentsByCreationDate(dateTimeNow);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByAuthor_returnsCommentsIfExist() {
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

        verify(userCommentRepository).findUserCommentsByAuthor(author);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByEvent_returnsCommentsIfExist() {
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

        verify(userCommentRepository).findUserCommentsByEvent(event);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentsByEventId_returnsCommentsIfExist() {
        when(userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(eventId))
                .thenReturn(userComments);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments))
                .thenReturn(userCommentDTOs);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEventId(eventId);

        assertNotNull(result);
        assertEquals(result, userCommentDTOs);
        assertEquals(2, result.size());

        verify(userCommentRepository).findUserCommentsByEventIdOrderByCreationDate(eventId);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userComments);
    }

    @Test
    void getUserCommentsByEventId_returnsEmptyListIfNoComments() {

        when(userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(eventId))
                .thenReturn(userCommentsEmpty);
        when(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userCommentsEmpty))
                .thenReturn(userCommentDTOsEmpty);

        List<UserCommentDTO> result = userCommentService.getUserCommentsByEventId(eventId);

        assertTrue(result.isEmpty());

        verify(userCommentRepository).findUserCommentsByEventIdOrderByCreationDate(eventId);
        verify(userCommentDTOUserCommentMapper).mapUserCommentsToUserCommentDTOs(userCommentsEmpty);
    }

    @Test
    void getUserCommentByEventIdAndCommentId_returnsCommentIfExists() {
        when(userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId1))
                .thenReturn(Optional.of(userComment1));
        when(userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment1))
                .thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId1);

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(userCommentRepository).findUserCommentByEventIdAndId(eventId, commentId1);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void getUserCommentByEventIdAndCommentId_throwsExceptionIfNotFound() {
        when(userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId2))
                .thenReturn(Optional.empty());

        assertThrows(UserCommentNotFoundException.class, () -> userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId2));


        verify(userCommentRepository).findUserCommentByEventIdAndId(eventId, commentId2);
        verify(userCommentDTOUserCommentMapper, never()).mapUserCommentToUserCommentDTO(any());
    }

    @Test
    void addNewUserCommentToEventById_addsNewComment() {
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userService.getUserById(author.getId())).thenReturn(author);
        when(userCommentDTOUserCommentMapper
                .mapUserCommentDTOToUserComment(userCommentDTO1, author, event)).thenReturn(userComment1);
        when(userCommentRepository.save(userComment1)).thenReturn(userComment1);
        when(userCommentDTOUserCommentMapper
                .mapUserCommentToUserCommentDTO(userComment1)).thenReturn(userCommentDTO1);

        UserCommentDTO result = userCommentService.addNewUserCommentToEventById(eventId, userCommentDTO1);

        assertNotNull(result);
        assertEquals(result, userCommentDTO1);

        verify(eventService).getEventById(eventId);
        verify(userService).getUserById(userCommentDTO1.getAuthorId());
        verify(userCommentRepository).save(userComment1);
        verify(userCommentDTOUserCommentMapper).mapUserCommentDTOToUserComment(userCommentDTO1, author, event);
        verify(userCommentDTOUserCommentMapper).mapUserCommentToUserCommentDTO(userComment1);
    }

    @Test
    void addNewUserCommentToEventById_throwsExceptionIfEventNotFound() {
        when(eventService.getEventById(eventId))
                .thenThrow(new EventNotFoundException(eventId));

        assertThrows(EventNotFoundException.class, () -> {
            userCommentService.addNewUserCommentToEventById(eventId, userCommentDTO1);
        });

        verify(eventService).getEventById(eventId);
        verify(userService, never()).getUserById(any());
        verify(userCommentRepository, never()).save(any());
    }

    @Test
    void addNewUserCommentToEventById_throwsExceptionIfAuthorNotFound() {
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(userService.getUserById(userCommentDTO1.getAuthorId()))
                .thenThrow(new UserNotFoundException(userCommentDTO1.getAuthorId()));

        assertThrows(UserNotFoundException.class, () -> {
            userCommentService.addNewUserCommentToEventById(eventId, userCommentDTO1);
        });

        verify(eventService).getEventById(eventId);
        verify(userService).getUserById(userCommentDTO1.getAuthorId());
        verify(userCommentRepository, never()).save(any());
    }

}