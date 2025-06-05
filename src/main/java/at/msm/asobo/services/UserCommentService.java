package at.msm.asobo.services;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserCommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserCommentService {
    private final UserCommentRepository userCommentRepository;
    private final EventRepository eventRepository;

    public UserCommentService(EventRepository eventRepository, UserCommentRepository userCommentRepository) {
        this.eventRepository = eventRepository;
        this.userCommentRepository = userCommentRepository;
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

        UserComment newComment = new UserComment(userCommentDTO);
        newComment.setEvent(event);
        return new UserCommentDTO(this.userCommentRepository.save(newComment));
    }

    public UserCommentDTO updateUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId, UserCommentDTO updatedCommentDTO) {
        UserComment existingComment = userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId)
                .orElseThrow(() -> new UserCommentNotFoundException(commentId));

        existingComment.setText(updatedCommentDTO.getText());
        existingComment.setModificationDate(LocalDateTime.now());
        existingComment.setAuthor(new User(updatedCommentDTO.getAuthor()));

        return new UserCommentDTO(userCommentRepository.save(existingComment));
    }


    public UserCommentDTO deleteUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
        UserCommentDTO userCommentDTO = this.getUserCommentByEventIdAndCommentId(eventId, commentId);
        UserComment userComment = new UserComment(userCommentDTO);
        this.userCommentRepository.delete(userComment);
        return userCommentDTO;
    }
}
