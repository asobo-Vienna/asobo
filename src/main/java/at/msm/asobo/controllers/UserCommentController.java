package at.msm.asobo.controllers;

import at.msm.asobo.entities.UserComment;
import at.msm.asobo.services.UserCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public List<UserComment> getAllUserComments(@PathVariable String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            return this.userCommentService.getUserCommentsByEventId(eventUUID);
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format: " + eventId);
        }
    }

    @GetMapping("/{id}")
    public UserComment getUserCommentById(@PathVariable String eventId, UUID id) {
        return this.userCommentService.getUserCommentById(id);
    }

    
}
