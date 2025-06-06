package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.mapper.UserCommentDTOUserCommentMapper;
import at.msm.asobo.mapper.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserCommentService {
    private final UserCommentRepository userCommentRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
    private final UserDTOUserMapper userDTOUserMapper;

    public UserCommentService(EventRepository eventRepository,
                              UserCommentRepository userCommentRepository,
                              UserService userService,
                              UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
                              UserDTOUserMapper userDTOUserMapper) {
        this.eventRepository = eventRepository;
        this.userCommentRepository = userCommentRepository;
        this.userService = userService;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    public List<UserCommentDTO> getAllUserComments() {
        return this.userCommentRepository.findAll().stream().map(UserCommentDTO::new).toList();
    }

    public UserCommentDTO getUserCommentById(UUID id) {
        UserComment userComment = this.userCommentRepository.findById(id).orElseThrow(() -> new UserCommentNotFoundException(id));
        return new UserCommentDTO(userComment);
    }

    public List<UserCommentDTO> getUserCommentsByCreationDate(LocalDateTime date) {
        return this.userCommentRepository.findUserCommentsByCreationDate(date).stream().map(UserCommentDTO::new).toList();
    }

    public List<UserCommentDTO> getUserCommentsByAuthor(User author) {
        return this.userCommentRepository.findUserCommentsByAuthor(author).stream().map(UserCommentDTO::new).toList();
    }

    public List<UserCommentDTO> getUserCommentsByEvent(Event event) {
        return this.userCommentRepository.findUserCommentsByEvent(event).stream().map(UserCommentDTO::new).toList();
    }

    public List<UserCommentDTO> getUserCommentsByEventId(UUID eventId) {
        return this.userCommentRepository.findUserCommentsByEventId(eventId).stream().map(UserCommentDTO::new).toList();
    }

    public UserCommentDTO getUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
        UserComment userComment = this.userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId).orElseThrow(() -> new UserCommentNotFoundException(commentId));
        return new UserCommentDTO(userComment);
    }

    public UserCommentDTO addNewUserCommentToEventById(UUID eventId, UserCommentDTO userCommentDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        UserComment newComment = this.userCommentDTOUserCommentMapper.mapUserCommentDTOToUserComment(userCommentDTO);
        newComment.setEvent(event);
        return new UserCommentDTO(this.userCommentRepository.save(newComment));
    }

    public UserCommentDTO updateUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId, UserCommentDTO updatedCommentDTO) {
        UserComment existingComment = userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId)
                .orElseThrow(() -> new UserCommentNotFoundException(commentId));

        existingComment.setText(updatedCommentDTO.getText());
        existingComment.setModificationDate(LocalDateTime.now());
        UUID authorId = updatedCommentDTO.getAuthorId();
        existingComment.setAuthor(this.userService.getUserById(authorId));

        return new UserCommentDTO(userCommentRepository.save(existingComment));
    }


    public UserCommentDTO deleteUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
        UserCommentDTO userCommentDTO = this.getUserCommentByEventIdAndCommentId(eventId, commentId);
        UserComment userComment = this.userCommentDTOUserCommentMapper.mapUserCommentDTOToUserComment(userCommentDTO);
        this.userCommentRepository.delete(userComment);
        return userCommentDTO;
    }
}
