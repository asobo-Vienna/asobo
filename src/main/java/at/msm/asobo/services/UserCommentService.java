package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.repositories.UserCommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserCommentService {
    private final UserCommentRepository userCommentRepository;

    public UserCommentService(UserCommentRepository userCommentRepository) {
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

    public List<UserComment> getUserCommentsByCreator(User user) {
        return this.userCommentRepository.findUserCommentsByCreator(user);
    }

    public List<UserComment> getUserCommentsByEvent(Event event) {
        return  this.userCommentRepository.findUserCommentsByEvent(event);
    }

    public UserComment addNewUserComment(UserComment userComment) {
        return this.userCommentRepository.save(userComment);
    }

    public UserComment deleteUserComment(UUID id) {
        UserComment userComment = this.getUserCommentById(id);
        this.userCommentRepository.delete(userComment);
        return userComment;
    }
}
