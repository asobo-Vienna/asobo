package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserBasicDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventAdminService;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/admins")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
public class EventAdminController {

  private final EventAdminService eventAdminService;

  public EventAdminController(EventAdminService eventAdminService) {
    this.eventAdminService = eventAdminService;
  }

  @GetMapping()
  @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public Set<UserPublicDTO> getEventAdmins(
      @PathVariable UUID eventId, @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return eventAdminService.getAllEventAdminsByEventId(eventId);
  }

  @PatchMapping()
  @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public Set<UserBasicDTO> addEventAdmins(
      @PathVariable UUID eventId,
      @RequestBody Set<UUID> userIds,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return eventAdminService.addAdminsToEvent(eventId, userIds, loggedInUser);
  }

  @DeleteMapping()
  @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
  public Set<UserBasicDTO> removeEventAdmins(
      @PathVariable UUID eventId,
      @RequestBody Set<UUID> userIds,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return eventAdminService.removeAdminsFromEvent(eventId, userIds, loggedInUser);
  }
}
