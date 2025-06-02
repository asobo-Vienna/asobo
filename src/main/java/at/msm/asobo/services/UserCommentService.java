package at.msm.asobo.services;

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

    public List<UserComment> getAllUserComments() {
        return this.userCommentRepository.findAll();
    }

    public UserComment getUserCommentById(UUID id) {
        return this.userCommentRepository.findById(id).orElseThrow(() -> new UserCommentNotFoundException(id));
    }

    public List<UserComment> getUserCommentsByCreationDate(LocalDateTime date) {
        return this.userCommentRepository.findUserCommentsByCreationDate(date);
    }

    public List<UserComment> getUserCommentsByAuthor(User author) {
        return this.userCommentRepository.findUserCommentsByAuthor(author);
    }

    public List<UserComment> getUserCommentsByEvent(Event event) {
        return this.userCommentRepository.findUserCommentsByEvent(event);
    }

    public List<UserComment> getUserCommentsByEventId(UUID eventId) {
        return this.userCommentRepository.findUserCommentsByEventId(eventId);
    }

    public UserComment getUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
        return this.userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId).orElseThrow(() -> new UserCommentNotFoundException(commentId));
    }

    public UserComment addNewUserCommentToEventById(UUID eventId, UserComment userComment) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        userComment.setEvent(event);  // Set the owning side of the relationship
        return this.userCommentRepository.save(userComment);
    }

    public UserComment updateUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId, UserComment updatedComment) {
        UserComment existingComment = userCommentRepository.findUserCommentByEventIdAndId(eventId, commentId)
                .orElseThrow(() -> new UserCommentNotFoundException(commentId));

        // Copy over the fields you allow to be updated
        existingComment.setText(updatedComment.getText());
        existingComment.setModificationDate(LocalDateTime.now());
        existingComment.setAuthor(updatedComment.getAuthor());

        return userCommentRepository.save(existingComment);
    }


    public UserComment deleteUserCommentByEventIdAndCommentId(UUID eventId, UUID commentId) {
        UserComment userComment = this.getUserCommentByEventIdAndCommentId(eventId, commentId);
        this.userCommentRepository.delete(userComment);
        return userComment;
    }
}
