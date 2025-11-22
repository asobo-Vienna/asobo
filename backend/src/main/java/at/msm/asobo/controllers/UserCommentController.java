package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.services.UserCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/comments")
public class UserCommentController {
    private final UserCommentService userCommentService;

    public UserCommentController(UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @GetMapping
    public List<UserCommentDTO> getAllUserComments(@PathVariable UUID eventId) {
        return this.userCommentService.getUserCommentsByEventId(eventId);
    }

    @GetMapping("/{commentId}")
    public UserCommentDTO getUserCommentById(@PathVariable UUID eventId, @PathVariable UUID commentId) {
        return this.userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCommentDTO addNewComment(@PathVariable UUID eventId, @RequestBody @Valid UserCommentDTO commentDTO) {
        return this.userCommentService.addNewUserCommentToEventById(eventId, commentDTO);
    }

    @PutMapping("/{commentId}")
    public UserCommentDTO updateUserComment(@PathVariable UUID eventId, @PathVariable UUID commentId,
                                            @RequestBody @Valid UserCommentDTO updatedCommentDTO) {
        return this.userCommentService.updateUserCommentByEventIdAndCommentId(eventId, commentId, updatedCommentDTO);
    }

    @DeleteMapping("/{commentId}")
    public UserCommentDTO deleteUserComment(@PathVariable UUID eventId, @PathVariable UUID commentId) {
        return this.userCommentService.deleteUserCommentByEventIdAndCommentId(eventId, commentId);
    }
}
