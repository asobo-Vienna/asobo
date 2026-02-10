package at.msm.asobo.controllers;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.MediumService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/media")
public class MediumController {

  private final MediumService mediumService;

  public MediumController(MediumService mediumService) {
    this.mediumService = mediumService;
  }

  @GetMapping()
  public List<MediumDTO> getAllMediaByEventId(@PathVariable UUID eventId) {
    return mediumService.getAllMediaByEventId(eventId);
  }

  @GetMapping("/{mediumId}")
  public MediumDTO getMediumById(@PathVariable UUID mediumId, @PathVariable UUID eventId) {
    return this.mediumService.getMediumDTOByIdAndEventId(mediumId, eventId);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  public MediumDTO addMediumToEventById(
      @PathVariable UUID eventId,
      @ModelAttribute @Valid MediumCreationDTO medium,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.mediumService.addMediumToEventById(eventId, medium, loggedInUser);
  }

  @DeleteMapping("/{mediumID}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
  public MediumDTO deleteMediumById(
      @PathVariable UUID mediumID,
      @PathVariable UUID eventId,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.mediumService.deleteMediumById(mediumID, eventId, loggedInUser);
  }
}
