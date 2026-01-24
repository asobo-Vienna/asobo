package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.ParticipantService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    // returns updated participants list
    @PostMapping()
    public Set<UserPublicDTO> toggleParticipantInEvent(@PathVariable UUID eventId,
                                                       @AuthenticationPrincipal UserPrincipal loggedInUser) {
        return this.participantService.toggleParticipantInEvent(eventId, loggedInUser);
    }

    @GetMapping()
    public Set<UserPublicDTO> getParticipantsByEventId(@PathVariable UUID eventId) {
        return this.participantService.getAllParticipantsAsDTOsByEventId(eventId);
    }
}
