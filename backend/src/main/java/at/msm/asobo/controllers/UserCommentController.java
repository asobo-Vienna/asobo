package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserCommentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/comments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
public class UserCommentController {
  private final UserCommentService userCommentService;

  public UserCommentController(UserCommentService userCommentService) {
    this.userCommentService = userCommentService;
  }

  @GetMapping
  public List<UserCommentDTO> getAllUserCommentsByEventId(@PathVariable UUID eventId) {
    return this.userCommentService.getUserCommentsByEventId(eventId);
  }

  @GetMapping("/paginated")
  public Page<UserCommentDTO> getAllUserCommentsByEventIdPaginated(
      @PathVariable UUID eventId,
      @PageableDefault(sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return this.userCommentService.getUserCommentsByEventIdPaginated(eventId, pageable);
  }

  @GetMapping("/{commentId}")
  public UserCommentDTO getUserCommentById(
      @PathVariable UUID eventId, @PathVariable UUID commentId) {
    return this.userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserCommentDTO addNewComment(
      @PathVariable UUID eventId, @RequestBody @Valid UserCommentDTO commentDTO) {
    return this.userCommentService.addNewUserCommentToEventById(eventId, commentDTO);
  }

  @PutMapping("/{commentId}")
  public UserCommentDTO updateUserComment(
      @PathVariable UUID eventId,
      @PathVariable UUID commentId,
      @RequestBody @Valid UserCommentDTO updatedCommentDTO,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.userCommentService.updateUserCommentByEventIdAndCommentId(
        eventId, commentId, updatedCommentDTO, loggedInUser);
  }

  @DeleteMapping("/{commentId}")
  public UserCommentDTO deleteUserComment(
      @PathVariable UUID eventId,
      @PathVariable UUID commentId,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.userCommentService.deleteUserCommentByEventIdAndCommentId(
        eventId, commentId, loggedInUser);
  }
}
