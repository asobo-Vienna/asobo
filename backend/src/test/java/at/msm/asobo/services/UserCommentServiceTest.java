package at.msm.asobo.services;

import at.msm.asobo.builders.UserCommentTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommentServiceTest {

    @Mock
    UserCommentRepository userCommentRepository;

    @Mock
    EventService eventService;

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

    @BeforeEach
    void setUp() {
        commentId1 = UUID.randomUUID();
        commentId2 = UUID.randomUUID();

        dateTimeNow = LocalDateTime.now();

        userComment1 = new UserCommentTestBuilder()
                .withId(commentId1)
                .withText("Test comment #1")
                .buildUserComment();

        userComment2 = new UserCommentTestBuilder()
                .withId(commentId2)
                .withText("Test comment #2")
                .buildUserComment();

        userCommentDTO1 = new UserCommentTestBuilder()
                .fromUserComment(userComment1)
                .buildUserCommentDTO();

        userCommentDTO2 = new UserCommentTestBuilder()
                .fromUserComment(userComment2)
                .buildUserCommentDTO();

        userComments = List.of(userComment1, userComment2);
        userCommentDTOs = List.of(userCommentDTO1, userCommentDTO2);

        author = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("TestAuthor")
                .buildUserEntity();
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

        assertThrows(UserCommentNotFoundException.class, () -> {
            userCommentService.getUserCommentDTOById(commentId1);
        });

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

        assertThrows(UserCommentNotFoundException.class, () -> {
            userCommentService.getUserCommentById(commentId1);
        });

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

        assertEquals(result.size(), 1);
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
}