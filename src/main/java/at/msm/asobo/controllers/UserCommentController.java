package at.msm.asobo.controllers;

import at.msm.asobo.entities.UserComment;
import at.msm.asobo.services.UserCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{commentId}")
    public UserComment getUserCommentById(@PathVariable UUID eventId, @PathVariable UUID commentId) {
        return this.userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserComment addNewComment(@PathVariable UUID eventId, @RequestBody @Valid UserComment comment) {
        return this.userCommentService.addNewUserCommentToEventById(eventId, comment);
    }

    @PutMapping("/{commentId}")
    public UserComment updateUserComment(@PathVariable UUID eventId, @PathVariable UUID commentId, @RequestBody @Valid UserComment updatedComment) {
        return this.userCommentService.updateUserCommentByEventIdAndCommentId(eventId, commentId, updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public UserComment deleteUserComment(@PathVariable UUID eventId, @PathVariable UUID commentId) {
        return this.userCommentService.deleteUserCommentByEventIdAndCommentId(eventId, commentId);
    }
}
