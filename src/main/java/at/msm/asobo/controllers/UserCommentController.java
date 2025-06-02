package at.msm.asobo.controllers;

import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.services.UserCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/comments")
public class UserCommentController {
    private UserCommentService userCommentService;

    public UserCommentController(UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @GetMapping
    public List<UserComment> getAllUserComments(@PathVariable UUID eventId) {
        return this.userCommentService.getUserCommentsByEventId(eventId);
    }

    @GetMapping("/{id}")
    public UserComment getUserCommentById(@PathVariable UUID eventId, @PathVariable UUID id) {
        return this.userCommentService.getUserCommentByEventIdAndCommentId(eventId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserComment addNewComment(@PathVariable UUID eventId, @RequestBody @Valid UserComment comment) {
        return this.userCommentService.addNewUserCommentToEventById(eventId, comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserComment> deleteUserComment(@PathVariable UUID eventId, @PathVariable UUID id) {
        UserComment deletedComment = userCommentService.deleteUserCommentByEventIdAndCommentId(eventId, id);
        return ResponseEntity.ok(deletedComment);
    }
}
