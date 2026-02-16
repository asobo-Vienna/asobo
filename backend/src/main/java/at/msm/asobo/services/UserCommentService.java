package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.mappers.UserCommentDTOUserCommentMapper;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserCommentService {
  private final UserCommentRepository userCommentRepository;
  private final UserService userService;
  private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
  private final EventService eventService;
  private final AccessControlService accessControlService;

  public UserCommentService(
      UserCommentRepository userCommentRepository,
      UserService userService,
      UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
      EventService eventService,
      AccessControlService accessControlService) {
    this.userCommentRepository = userCommentRepository;
    this.userService = userService;
    this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
    this.eventService = eventService;
    this.accessControlService = accessControlService;
  }

  public UserCommentDTO getUserCommentDTOById(UUID id) {
    UserComment userComment =
        this.userCommentRepository
            .findById(id)
            .orElseThrow(() -> new UserCommentNotFoundException(id));
    return this.userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment);
  }

  public UserComment getUserCommentById(UUID id) {
    return this.userCommentRepository
        .findById(id)
        .orElseThrow(() -> new UserCommentNotFoundException(id));
  }

  public List<UserCommentDTO> getUserCommentsByCreationDate(LocalDateTime date) {
    List<UserComment> userComments =
        this.userCommentRepository.findUserCommentsByCreationDate(date);
    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public List<UserCommentDTO> getUserCommentsByAuthor(User author) {
    List<UserComment> userComments = this.userCommentRepository.findUserCommentsByAuthor(author);
    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public List<UserCommentDTO> getUserCommentsByEvent(Event event) {
    List<UserComment> userComments = this.userCommentRepository.findUserCommentsByEvent(event);
    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public List<UserCommentDTO> getUserCommentsByEventId(UUID eventId) {
    List<UserComment> userComments =
        this.userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(eventId);
    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public Page<UserCommentDTO> getUserCommentsByEventIdPaginated(UUID eventId, Pageable pageable) {
    Page<UserComment> userComments =
        this.userCommentRepository.findUserCommentsByEventIdOrderByCreationDate(eventId, pageable);

    return this.userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(userComments);
  }

  public UserCommentDTO getUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
    UserComment userComment =
        this.userCommentRepository
            .findUserCommentByEventIdAndId(eventId, commentId)
            .orElseThrow(() -> new UserCommentNotFoundException(commentId));
    return this.userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(userComment);
  }

  public UserCommentDTO addNewUserCommentToEventById(UUID eventId, UserCommentDTO userCommentDTO) {
    Event event = eventService.getEventById(eventId);
    User author = userService.getUserById(userCommentDTO.getAuthorId());

    UserComment newComment =
        this.userCommentDTOUserCommentMapper.mapUserCommentDTOToUserComment(
            userCommentDTO, author, event);

    UserComment savedComment = userCommentRepository.save(newComment);
    return this.userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(savedComment);
  }

  public UserCommentDTO updateUserCommentByEventIdAndCommentId(
      UUID eventId, UUID commentId, UserCommentDTO updatedCommentDTO, UserPrincipal userPrincipal) {
    UserComment existingComment =
        userCommentRepository
            .findUserCommentByEventIdAndId(eventId, commentId)
            .orElseThrow(() -> new UserCommentNotFoundException(commentId));
    User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

    this.accessControlService.assertCanUpdateComment(existingComment, loggedInUser);

    existingComment.setText(updatedCommentDTO.getText());
    UserComment savedExistingComment = userCommentRepository.save(existingComment);

    return this.userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(
        savedExistingComment);
  }

  public UserCommentDTO deleteUserCommentByEventIdAndCommentId(
      UUID eventId, UUID commentId, UserPrincipal userPrincipal) {
    UserComment existingComment =
        userCommentRepository
            .findUserCommentByEventIdAndId(eventId, commentId)
            .orElseThrow(() -> new UserCommentNotFoundException(commentId));
    User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

    this.accessControlService.assertCanDeleteComment(existingComment, loggedInUser);

    UserCommentDTO commentToDelete =
        userCommentDTOUserCommentMapper.mapUserCommentToUserCommentDTO(existingComment);
    this.userCommentRepository.delete(existingComment);
    return commentToDelete;
  }
}
